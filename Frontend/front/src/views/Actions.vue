<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuth } from '../composables/useAuth.js'
import { useDisplay } from 'vuetify'

const { authHeaders } = useAuth()
const { smAndDown } = useDisplay()

const API_BASE = 'https://api-ptut.up.railway.app'

const TYPE_CONFIG = {
    'SALON': { label: 'SALON ÉTUDIANT', color: 'salon' },
    'LYCEE': { label: 'LYCÉE', color: 'lycee' },
    'RESEAUX_SOCIAUX': { label: 'RÉSEAUX SOCIAUX', color: 'reseaux' },
    'FORMATION': { label: 'FORMATION', color: 'formation' }
}

function getActionStyle(typeBrut) {
    if (!typeBrut) return { label: 'AUTRE', color: 'grey' };
    const key = typeBrut
        .normalize("NFD").replace(/[\u0300-\u036f]/g, "")
        .replace(/\s+/g, '_')
        .toUpperCase();
    if (TYPE_CONFIG[key]) return TYPE_CONFIG[key];
    if (key.includes('SALON')) return TYPE_CONFIG['SALON'];
    return { label: typeBrut, color: 'grey' };
}

// ── Preuves en attente ──
const preuves = ref([])
const loadingPreuves = ref(true)

async function chargerPreuves() {
    loadingPreuves.value = true
    try {
        const res = await fetch(`${API_BASE}/actions/inscriptions/dossiers-en-cours`, {
            headers: authHeaders()
        })
        const data = await res.json()
        preuves.value = data.map(p => ({
            idInscription: p.idInscription,
            idAction: p.idAction,
            etudiant: `${p.prenomAmbassadeur} ${p.nomAmbassadeur}`,
            action: p.titreAction,
            type: p.typeAction,
            justificatifId: p.justificatifId || null,
            fichier: p.nomFichier || 'justificatif.pdf'
        }))
    } catch (e) {
        console.error("Erreur API Preuves:", e.message)
    } finally {
        loadingPreuves.value = false
    }
}

async function validerPreuve(idAction, idInscription) {
    try {
        const res = await fetch(`${API_BASE}/actions/${idAction}/inscriptions/${idInscription}/valider`, {
            method: 'PUT',
            headers: authHeaders()
        })
        if (!res.ok) throw new Error("Erreur lors de la validation")
        preuves.value = preuves.value.filter(p => p.idInscription !== idInscription)
        await chargerEtudiants()
    } catch (e) {
        alert("Impossible de valider la preuve.")
    }
}

// ── Suivi d'activités ──
const etudiants = ref([])
const loading = ref(true)
const recherche = ref('')

// ── Dialog étudiant ──
const dialogEtudiant = ref(false)
const etudiantSelec = ref(null)

async function chargerEtudiants() {
    loading.value = true
    try {
        const res = await fetch(`${API_BASE}/ambassadeurs`, { headers: authHeaders() })
        const data = await res.json()
        etudiants.value = data.map(e => {
            const actionsFormatees = (e.actions || []).map(a => ({
                id: a.idAction || a.id,
                titre: a.titre,
                date: a.dateAction || a.date || 'Date non définie',
                type: a.typeAction || a.type,
                statut: a.statutInscription || a.statut || 'INSCRIT',
                justificatifUrl: a.justificatifUrl || null
            }))
            return {
                id: e.idUtilisateur || e.id,
                prenom: e.prenom,
                nom: e.nom,
                email: e.email,
                actions: actionsFormatees
            }
        })
    } catch (e) {
        console.error("Erreur API Étudiants:", e.message)
    } finally {
        loading.value = false
    }
}

const etudiantsFiltres = computed(() => {
    const q = recherche.value.toLowerCase().trim()
    if (!q) return etudiants.value
    return etudiants.value.filter(e => `${e.prenom} ${e.nom}`.toLowerCase().includes(q))
})

async function ouvrirJustificatif(justificatifId) {
    if (!justificatifId) return
    try {
        const res = await fetch(`${API_BASE}/actions/${justificatifId}/justificatifs`, {
            headers: authHeaders()
        })
        if (!res.ok) throw new Error('Accès refusé')
        const blob = await res.blob()
        const url = URL.createObjectURL(blob)
        window.open(url, '_blank')
    } catch (e) {
        alert('Impossible d ouvrir le justificatif : ' + e.message)
    }
}

function ouvrirDialog(etudiant) {
    etudiantSelec.value = etudiant
    dialogEtudiant.value = true
}

onMounted(() => {
    chargerPreuves()
    chargerEtudiants()
})
</script>

<template>
    <v-container class="py-8" :class="smAndDown ? 'px-3' : ''" max-width="800">

        <!-- Preuves à valider -->
        <div class="mb-10">
            <div class="d-flex align-center mb-4 ga-3">
                <h2 class="text-h5 font-weight-bold">Preuves à valider</h2>
                <v-chip color="warning" variant="tonal" size="small" class="font-weight-bold">
                    {{ preuves.length }}
                </v-chip>
            </div>

            <div v-if="loadingPreuves" class="d-flex justify-center py-6">
                <v-progress-circular indeterminate color="bleu" />
            </div>

            <v-card v-else v-for="preuve in preuves" :key="preuve.idInscription" rounded="lg" border elevation="0"
                class="mb-3">
                <v-card-text class="pa-4">
                    <div class="d-flex" :class="smAndDown ? 'flex-column ga-3' : 'align-center ga-3'">
                        <div class="d-flex align-center ga-3 flex-grow-1 overflow-hidden">
                            <v-avatar color="blue-grey-lighten-4" rounded="lg" size="44">
                                <v-icon color="blue-grey-darken-2">mdi-file-image</v-icon>
                            </v-avatar>
                            <div class="overflow-hidden">
                                <div class="text-body-2 font-weight-bold text-truncate">{{ preuve.etudiant }}</div>
                                <div class="text-caption text-medium-emphasis text-truncate">{{ preuve.action }}</div>
                            </div>
                        </div>

                        <div class="d-flex align-center" :class="smAndDown ? 'justify-space-between w-100' : 'ga-2'">
                            <v-chip :color="getActionStyle(preuve.type).color" variant="outlined" size="x-small" label
                                class="font-weight-bold">
                                {{ getActionStyle(preuve.type).label }}
                            </v-chip>

                            <v-btn v-if="preuve.justificatifId" variant="outlined" size="small" rounded="xl"
                                @click="ouvrirJustificatif(preuve.justificatifId)">
                                <v-icon start size="16">mdi-eye</v-icon>Voir
                            </v-btn>

                            <v-btn color="success" variant="flat" size="small" rounded="xl"
                                @click="validerPreuve(preuve.idAction, preuve.idInscription)">
                                <v-icon start size="16">mdi-check</v-icon>Valider
                            </v-btn>
                        </div>
                    </div>
                </v-card-text>
            </v-card>
        </div>

        <v-divider class="mb-10" />

        <!-- Suivi d'activités -->
        <h1 class="text-h4 font-weight-bold text-center mb-6">Suivi d'activités</h1>
        <v-text-field v-model="recherche" placeholder="Rechercher un étudiant..." variant="outlined" rounded="xl"
            density="comfortable" prepend-inner-icon="mdi-magnify" hide-details class="mb-6 mx-auto"
            style="max-width: 400px;" />

        <div v-if="loading" class="d-flex justify-center py-12">
            <v-progress-circular indeterminate color="bleu" />
        </div>

        <template v-else>
            <v-list lines="one" rounded="lg" border class="mb-6 pa-0 overflow-hidden">
                <v-list-item v-for="etudiant in etudiantsFiltres" :key="etudiant.id" @click="ouvrirDialog(etudiant)"
                    class="py-2">
                    <template #prepend>
                        <v-avatar color="bleu" size="36">
                            <span class="text-caption font-weight-bold text-white">
                                {{ etudiant.prenom[0] }}{{ etudiant.nom[0] }}
                            </span>
                        </v-avatar>
                    </template>
                    <v-list-item-title class="font-weight-bold text-body-2">
                        {{ etudiant.prenom }} {{ etudiant.nom }}
                    </v-list-item-title>
                    <v-list-item-subtitle class="text-caption">
                        {{ etudiant.actions.length }} action(s)
                    </v-list-item-subtitle>
                    <template #append>
                        <v-icon size="20">mdi-chevron-right</v-icon>
                    </template>
                </v-list-item>
            </v-list>
        </template>
    </v-container>

    <!-- Dialog détail étudiant -->
    <v-dialog v-model="dialogEtudiant" max-width="560" scrollable>
        <v-card v-if="etudiantSelec" rounded="xl">
            <v-card-title class="pa-4 pb-2 d-flex align-center ga-3">
                <v-avatar color="bleu" size="40">
                    <span class="text-body-2 font-weight-bold text-white">
                        {{ etudiantSelec.prenom[0] }}{{ etudiantSelec.nom[0] }}
                    </span>
                </v-avatar>
                <div>
                    <div class="font-weight-bold text-body-1">{{ etudiantSelec.prenom }} {{ etudiantSelec.nom }}</div>
                    <div class="text-caption text-medium-emphasis">Historique des missions</div>
                </div>
                <v-spacer />
                <v-btn icon variant="text" size="small" @click="dialogEtudiant = false">
                    <v-icon>mdi-close</v-icon>
                </v-btn>
            </v-card-title>

            <v-divider />

            <v-card-text class="pa-3">
                <v-list v-if="etudiantSelec.actions.length > 0" lines="two" rounded="lg" border class="pa-0">
                    <v-list-item v-for="action in etudiantSelec.actions" :key="action.id" class="px-3 py-2 border-b">
                        <div class="d-flex flex-column ga-1 py-1">
                            <div class="d-flex align-center justify-space-between ga-2">
                                <v-chip :color="getActionStyle(action.type).color" variant="outlined" size="x-small"
                                    label class="font-weight-bold">
                                    {{ getActionStyle(action.type).label }}
                                </v-chip>
                                <v-chip :color="action.statut === 'VALIDE' ? 'success' : 'warning'" size="x-small"
                                    variant="flat" class="font-weight-bold">
                                    {{ action.statut === 'VALIDE' ? 'Validée' : 'Inscrit' }}
                                </v-chip>
                            </div>
                            <div class="text-body-2 font-weight-bold">{{ action.titre }}</div>
                            <div class="text-caption text-medium-emphasis">{{ action.date }}</div>
                        </div>
                    </v-list-item>
                </v-list>
                <div v-else class="text-center py-6 text-caption text-grey">Aucune action enregistrée.</div>
            </v-card-text>
        </v-card>
    </v-dialog>
</template>

<style scoped>
.text-truncate {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.border-b:last-child {
    border-bottom: none !important;
}
</style>