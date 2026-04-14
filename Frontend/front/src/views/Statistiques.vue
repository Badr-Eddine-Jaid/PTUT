<script setup>
import { ref, onMounted, computed } from 'vue'
import { useAuth } from '../composables/useAuth.js'

const { authHeaders } = useAuth()
const API_BASE = 'https://api-ptut.up.railway.app'

const loading = ref(true)
const error = ref(null)

const stats = ref({
    global: { totalActions: 0, totalAmbassadeurs: 0, totalPreuvesValidees: 0, totalPreuvesEnAttente: 0 },
    parType: [],
    parMois: []
})

onMounted(async () => {
    try {
        const res = await fetch(`${API_BASE}/statistiques`, {
            headers: authHeaders()
        })
        if (!res.ok) throw new Error(`Erreur serveur : ${res.status}`)
        stats.value = await res.json()
    } catch (e) {
        console.error('Erreur API:', e.message)
        error.value = "Erreur de liaison avec l'API Statistiques."
    } finally {
        loading.value = false
    }
})

// ── Calculs dynamiques pour les graphiques ──
const maxMois = computed(() => {
    if (!stats.value.parMois?.length) return 1
    return Math.max(...stats.value.parMois.map(m => m.actions))
})

const camembert = computed(() => {
    if (!stats.value.parType?.length) return []
    const total = stats.value.parType.reduce((s, t) => s + t.total, 0)
    let angle = 0
    return stats.value.parType.map(t => {
        const pct = t.total / total
        const start = angle
        angle += pct * 360
        const end = angle
        const r = 80, cx = 100, cy = 100
        const x1 = cx + r * Math.cos((start - 90) * Math.PI / 180)
        const y1 = cy + r * Math.sin((start - 90) * Math.PI / 180)
        const x2 = cx + r * Math.cos((end - 90) * Math.PI / 180)
        const y2 = cy + r * Math.sin((end - 90) * Math.PI / 180)
        return { ...t, path: `M ${cx} ${cy} L ${x1} ${y1} A ${r} ${r} 0 ${pct > 0.5 ? 1 : 0} 1 ${x2} ${y2} Z`, pct: Math.round(pct * 100) }
    })
})
</script>

<template>
    <v-container class="py-8" max-width="960">
        <h1 class="text-h4 font-weight-bold mb-8">Tableau de bord de pilotage</h1>

        <v-alert v-if="error" type="error" variant="tonal" class="mb-6">{{ error }}</v-alert>

        <div v-if="loading" class="d-flex justify-center py-12">
            <v-progress-circular indeterminate color="primary" />
        </div>

        <template v-else>
            <v-row class="mb-8">
                <v-col v-for="(v, k) in {
                    'Actions au total': [stats.global.totalActions, 'blue'],
                    'Ambassadeurs': [stats.global.totalAmbassadeurs, 'green'],
                    'Preuves validées': [stats.global.totalPreuvesValidees, 'success'],
                    'En attente': [stats.global.totalPreuvesEnAttente, 'warning']
                }" :key="k" cols="12" sm="6" md="3">
                    <v-card rounded="xl" border elevation="0" class="pa-5 text-center">
                        <div class="text-h3 font-weight-bold" :class="`text-${v[1]}`">{{ v[0] }}</div>
                        <div class="text-body-2 text-medium-emphasis mt-1">{{ k }}</div>
                    </v-card>
                </v-col>
            </v-row>

            <v-row class="mb-8">
                <v-col cols="12" md="7">
                    <v-card rounded="xl" border elevation="0" class="pa-6" height="100%">
                        <h2 class="text-h6 font-weight-bold mb-6">Répartition par type d'action</h2>
                        <div v-for="t in stats.parType" :key="t.type" class="mb-5">
                            <div class="d-flex justify-space-between mb-1">
                                <span class="text-body-2 font-weight-bold">{{ t.type }}</span>
                                <span class="text-caption">{{ t.total }} inscrits</span>
                            </div>
                            <v-progress-linear :model-value="(t.validees / t.total) * 100" :color="t.couleur"
                                height="10" rounded />
                            <div class="d-flex ga-4 mt-1">
                                <span class="text-caption text-success">✓ {{ t.validees }} validées</span>
                                <span class="text-caption text-warning">⏳ {{ t.enCours }} en cours</span>
                            </div>
                        </div>
                    </v-card>
                </v-col>

                <v-col cols="12" md="5">
                    <v-card rounded="xl" border elevation="0" class="pa-6" height="100%">
                        <h2 class="text-h6 font-weight-bold mb-4">Volume global</h2>
                        <div class="d-flex flex-column align-center">
                            <svg viewBox="0 0 200 200" width="180" height="180">
                                <path v-for="s in camembert" :key="s.type" :d="s.path" :fill="s.couleur" stroke="white"
                                    stroke-width="2" />
                            </svg>
                            <div class="mt-4 w-100">
                                <div v-for="s in camembert" :key="s.type" class="d-flex align-center ga-2 mb-2">
                                    <div :style="`background:${s.couleur}`"
                                        style="width:10px;height:10px;border-radius:2px;" />
                                    <span class="text-caption">{{ s.type }}</span>
                                    <v-spacer />
                                    <span class="text-caption font-weight-bold">{{ s.pct }}%</span>
                                </div>
                            </div>
                        </div>
                    </v-card>
                </v-col>
            </v-row>

            <v-card rounded="xl" border elevation="0" class="pa-6">
                <h2 class="text-h6 font-weight-bold mb-8">Inscriptions mensuelles</h2>
                <div class="d-flex align-end ga-4" style="height: 180px;">
                    <div v-for="m in stats.parMois" :key="m.mois" class="d-flex flex-column align-center flex-grow-1"
                        style="height: 100%;">
                        <span class="text-caption mb-2">{{ m.actions }}</span>
                        <div
                            :style="`height: ${(m.actions / maxMois) * 100}%; background: linear-gradient(to top, #0D47A1, #3CBEBE); border-radius: 8px 8px 0 0; width: 100%; transition: height 0.6s;`" />
                        <span class="text-caption mt-2 font-weight-bold">{{ m.mois }}</span>
                    </div>
                </div>
            </v-card>
        </template>
    </v-container>
</template>