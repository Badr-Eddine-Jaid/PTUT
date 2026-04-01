<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuth } from '../composables/useAuth.js'

const { authHeaders } = useAuth()

const API_BASE = 'https://api-ptut.up.railway.app'

const actions = ref([])
const loading = ref(true)

// Configuration unifiée des types d'actions
const TYPE_CONFIG = {
    'SALON': { label: 'SALON ÉTUDIANT', color: 'salon' },
    'LYCEE': { label: 'LYCÉE', color: 'lycee' },
    'RESEAUX_SOCIAUX': { label: 'RÉSEAUX SOCIAUX', color: 'reseaux' },
    'FORMATION': { label: 'FORMATION', color: 'formation' }
}

onMounted(async () => {
    try {
        // 🌟 L'ASTUCE : 1. On récupère d'abord toutes les actions pour avoir leur type
        const resActions = await fetch(`${API_BASE}/actions`, { headers: authHeaders() })
        const actionsData = await resActions.json()

        // On crée un petit dictionnaire { idAction: "TYPE" }
        const typesParActionId = {}
        actionsData.forEach(action => {
            typesParActionId[action.idAction] = action.typeAction
        })

        // 2. On récupère ensuite les inscriptions de l'étudiant
        const res = await fetch(`${API_BASE}/actions/inscriptions/me`, {
            headers: authHeaders()
        })
        if (!res.ok) throw new Error(`HTTP ${res.status}`)

        const data = await res.json()

        // 3. On assemble tout !
        actions.value = data.map(item => {
            // 🎯 CORRECTION : on utilise statutInscription (le vrai nom du Swagger)
            let statutLocal = 'a_venir'
            if (item.statutInscription === 'DOSSIER_EN_COURS_DE_TRAITEMENT') statutLocal = 'en_cours'
            if (item.statutInscription === 'VALIDE') statutLocal = 'validee'

            return {
                idInscription: item.idInscription,
                idAction: item.idAction,
                titre: item.titreAction || 'Titre en attente...',
                // 🎯 MAGIE : On utilise le dictionnaire pour avoir la bonne couleur !
                type: typesParActionId[item.idAction] || 'FORMATION',
                // Pour la date, on nettoie un peu l'affichage dégueulasse "2026-03..." si besoin
                date: item.dateAction || item.dateInscription.split('T')[0],
                lieu: item.lieuAction || '',

                statut: statutLocal,
                preuve: item.justificatifUrl || null
            }
        })
    } catch (e) {
        console.error('Erreur API mes-actions:', e.message)
    } finally {
        loading.value = false
    }
})

// ── Sections ──
const actionsAVenir = computed(() => actions.value.filter(a => a.statut === 'a_venir'))
const actionsEnCours = computed(() => actions.value.filter(a => a.statut === 'en_cours'))
const actionsValidee = computed(() => actions.value.filter(a => a.statut === 'validee'))

// ── Upload preuve ──
const dialogUpload = ref(false)
const actionSelectee = ref(null)
const fichierUpload = ref(null)
const uploadEnCours = ref(false)
const erreurUpload = ref('')

function ouvrirUpload(action) {
    actionSelectee.value = action
    fichierUpload.value = null
    erreurUpload.value = ''
    dialogUpload.value = true
}

async function soumettrePreuve() {
    if (!fichierUpload.value) return
    uploadEnCours.value = true
    erreurUpload.value = ''

    try {
        const formData = new FormData()
        formData.append('fichier', fichierUpload.value)

        const headers = authHeaders()
        delete headers['Content-Type'] // Indispensable pour l'upload de fichiers

        const res = await fetch(`${API_BASE}/actions/${actionSelectee.value.idAction}/justificatif`, {
            method: 'POST',
            headers,
            body: formData
        })

        if (!res.ok) throw new Error('Erreur lors de l\'envoi')

        // On met à jour l'interface instantanément
        const action = actions.value.find(a => a.idAction === actionSelectee.value.idAction)
        if (action) {
            action.statut = 'en_cours'
            action.preuve = fichierUpload.value.name
        }

        dialogUpload.value = false
        actionSelectee.value = null

    } catch (e) {
        erreurUpload.value = e.message
    } finally {
        uploadEnCours.value = false
    }
}
</script>

<template>
    <v-container class="py-8" max-width="800">

        <h1 class="text-h4 font-weight-bold mb-8">Mes actions</h1>

        <div v-if="loading" class="d-flex justify-center py-12">
            <v-progress-circular indeterminate color="primary" />
        </div>

        <template v-else>

            <div class="mb-8">
                <div class="d-flex align-center ga-3 mb-4">
                    <h2 class="text-h6 font-weight-bold">À venir</h2>
                    <v-chip color="blue" variant="tonal" size="small">{{ actionsAVenir.length }}</v-chip>
                </div>

                <div v-if="actionsAVenir.length === 0" class="text-medium-emphasis text-body-2 py-2">
                    Aucune action à venir.
                </div>

                <v-card v-for="action in actionsAVenir" :key="action.idInscription" rounded="lg" border elevation="0"
                    class="mb-3">
                    <v-card-text class="pa-4">
                        <div class="d-flex align-center ga-3">
                            <v-chip :color="TYPE_CONFIG[action.type]?.color || 'grey'" variant="outlined" size="small"
                                label style="min-width: 130px; justify-content: center;">
                                {{ TYPE_CONFIG[action.type]?.label || action.type }}
                            </v-chip>
                            <div class="flex-grow-1">
                                <div class="text-body-2 font-weight-semibold">{{ action.titre }}</div>
                                <div class="text-caption text-medium-emphasis">{{ action.date }}<span
                                        v-if="action.lieu"> · {{ action.lieu }}</span></div>
                            </div>
                            <v-btn color="bleu" variant="flat" rounded="xl" size="small" @click="ouvrirUpload(action)">
                                <v-icon class="mr-1" size="16">mdi-upload</v-icon>
                                Déposer ma preuve
                            </v-btn>
                        </div>
                    </v-card-text>
                </v-card>
            </div>

            <v-divider class="mb-8" />

            <div class="mb-8">
                <div class="d-flex align-center ga-3 mb-4">
                    <h2 class="text-h6 font-weight-bold">En cours de traitement</h2>
                    <v-chip color="warning" variant="tonal" size="small">{{ actionsEnCours.length }}</v-chip>
                </div>

                <div v-if="actionsEnCours.length === 0" class="text-medium-emphasis text-body-2 py-2">
                    Aucune action en cours de traitement.
                </div>

                <v-card v-for="action in actionsEnCours" :key="action.idInscription" rounded="lg" border elevation="0"
                    class="mb-3">
                    <v-card-text class="pa-4">
                        <div class="d-flex align-center ga-3">
                            <v-chip :color="TYPE_CONFIG[action.type]?.color || 'grey'" variant="outlined" size="small"
                                label style="min-width: 130px; justify-content: center;">
                                {{ TYPE_CONFIG[action.type]?.label || action.type }}
                            </v-chip>
                            <div class="flex-grow-1">
                                <div class="text-body-2 font-weight-semibold">{{ action.titre }}</div>
                                <div class="text-caption text-medium-emphasis">{{ action.date }}<span
                                        v-if="action.lieu"> · {{ action.lieu }}</span></div>
                                <div class="text-caption text-medium-emphasis mt-1" v-if="action.preuve">
                                    <v-icon size="12" class="mr-1">mdi-paperclip</v-icon>{{ action.preuve }}
                                </div>
                            </div>
                            <v-chip color="warning" variant="tonal" size="small">
                                <v-icon start size="14">mdi-clock-outline</v-icon>
                                En cours
                            </v-chip>
                        </div>
                    </v-card-text>
                </v-card>
            </div>

            <v-divider class="mb-8" />

            <div>
                <div class="d-flex align-center ga-3 mb-4">
                    <h2 class="text-h6 font-weight-bold">Actions validées</h2>
                    <v-chip color="success" variant="tonal" size="small">{{ actionsValidee.length }}</v-chip>
                </div>

                <div v-if="actionsValidee.length === 0" class="text-medium-emphasis text-body-2 py-2">
                    Aucune action validée pour l'instant.
                </div>

                <v-card v-for="action in actionsValidee" :key="action.idInscription" rounded="lg" border elevation="0"
                    class="mb-3">
                    <v-card-text class="pa-4">
                        <div class="d-flex align-center ga-3">
                            <v-chip :color="TYPE_CONFIG[action.type]?.color || 'grey'" variant="outlined" size="small"
                                label style="min-width: 130px; justify-content: center;">
                                {{ TYPE_CONFIG[action.type]?.label || action.type }}
                            </v-chip>
                            <div class="flex-grow-1">
                                <div class="text-body-2 font-weight-semibold">{{ action.titre }}</div>
                                <div class="text-caption text-medium-emphasis">{{ action.date }}<span
                                        v-if="action.lieu"> · {{ action.lieu }}</span></div>
                            </div>
                            <v-chip color="success" variant="tonal" size="small">
                                <v-icon start size="14">mdi-check-circle</v-icon>
                                Validée
                            </v-chip>
                        </div>
                    </v-card-text>
                </v-card>
            </div>

        </template>

    </v-container>

    <v-dialog v-model="dialogUpload" max-width="440">
        <v-card v-if="actionSelectee" rounded="xl">
            <v-card-title class="pa-5 pb-2 font-weight-bold">
                Déposer ma preuve
            </v-card-title>
            <v-card-subtitle class="px-5 pb-3 text-medium-emphasis">
                {{ actionSelectee.titre }}
            </v-card-subtitle>

            <v-card-text class="pa-5 pt-2">
                <v-alert v-if="erreurUpload" type="error" variant="tonal" density="compact" class="mb-3">
                    {{ erreurUpload }}
                </v-alert>
                <v-file-input v-model="fichierUpload" label="Sélectionner un fichier" variant="outlined" rounded="lg"
                    density="comfortable" accept="image/*,.pdf" prepend-icon="" prepend-inner-icon="mdi-paperclip"
                    hide-details />
            </v-card-text>

            <v-card-actions class="pa-5 pt-0 d-flex ga-2">
                <v-btn variant="text" rounded="xl" @click="dialogUpload = false">
                    Annuler
                </v-btn>
                <v-spacer />
                <v-btn color="bleu" variant="flat" rounded="xl" :loading="uploadEnCours" :disabled="!fichierUpload"
                    @click="soumettrePreuve">
                    Envoyer
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>