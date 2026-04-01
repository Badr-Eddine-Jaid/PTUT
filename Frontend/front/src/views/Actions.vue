<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuth } from '../composables/useAuth.js'

const { authHeaders } = useAuth()

const API_BASE = 'https://api-ptut.up.railway.app'

// Configuration des types d'actions (Enums API)
const TYPE_CONFIG = {
    'SALON': { label: 'SALON ÉTUDIANT', color: 'salon' },
    'LYCEE': { label: 'LYCÉE', color: 'lycee' },
    'RESEAUX_SOCIAUX': { label: 'RÉSEAUX SOCIAUX', color: 'reseaux' },
    'FORMATION': { label: 'FORMATION', color: 'formation' }
}

// ── Preuves en attente (Vraie API) ──
const preuves = ref([])
const loadingPreuves = ref(true)

async function chargerPreuves() {
    loadingPreuves.value = true
    try {
        const res = await fetch(`${API_BASE}/actions/inscriptions/dossiers-en-cours`, {
            headers: authHeaders()
        })
        if (!res.ok) throw new Error("Erreur lors du chargement des dossiers en cours")

        const data = await res.json()

        preuves.value = data.map(p => ({
            idInscription: p.idInscription,
            idAction: p.idAction,
            etudiant: `${p.prenomAmbassadeur} ${p.nomAmbassadeur}`,
            action: p.titreAction,
            type: p.typeAction,
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

        // On retire la preuve validée de l'affichage
        preuves.value = preuves.value.filter(p => p.idInscription !== idInscription)
    } catch (e) {
        console.error("Erreur Validation:", e.message)
        alert("Impossible de valider la preuve.")
    }
}

// ── Étudiants depuis l'API ──
const etudiants = ref([])
const loading = ref(true)

async function chargerEtudiants() {
    loading.value = true
    try {
        const res = await fetch(`${API_BASE}/ambassadeurs`, {
            headers: authHeaders()
        })
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        const data = await res.json()

        etudiants.value = data.map(e => {
            // 🎯 On traduit les variables du Back-end pour le Front-end
            const actionsFormatees = (e.actions || []).map(a => ({
                id: a.id || a.idAction,
                titre: a.titre || a.titreAction,
                date: a.date || a.dateAction,
                type: a.typeAction || a.type || 'FORMATION' // <-- C'est lui qui posait problème !
            }))

            return {
                id: e.id,
                prenom: e.prenom,
                nom: e.nom,
                actions: actionsFormatees
            }
        })
    } catch (e) {
        console.error("Erreur API Étudiants:", e.message)
    } finally {
        loading.value = false
    }
}

// Initialisation au chargement de la page
onMounted(() => {
    chargerPreuves()
    chargerEtudiants()
})

// ── Recherche ──
const recherche = ref('')

const etudiantsFiltres = computed(() => {
    const q = recherche.value.toLowerCase().trim()
    if (!q) return etudiants.value
    return etudiants.value.filter(e =>
        e.nom.toLowerCase().includes(q) ||
        e.prenom.toLowerCase().includes(q) ||
        `${e.prenom} ${e.nom}`.toLowerCase().includes(q)
    )
})

// ── Étudiant sélectionné ──
const etudiantSelec = ref(null)

function selectionner(etudiant) {
    etudiantSelec.value = etudiantSelec.value?.id === etudiant.id ? null : etudiant
}

// ── Preview fichier ──
const previewDialog = ref(false)
const previewFichier = ref(null)

function voirFichier(preuve) {
    previewFichier.value = preuve
    previewDialog.value = true
}
</script>

<template>
    <v-container class="py-8" max-width="700">

        <div class="mb-10">
            <div class="d-flex align-center mb-4 ga-3">
                <h2 class="text-h5 font-weight-bold">Preuves à valider</h2>
                <v-chip color="warning" variant="tonal" size="small">
                    {{ preuves.length }} en attente
                </v-chip>
            </div>

            <div v-if="loadingPreuves" class="d-flex justify-center py-6">
                <v-progress-circular indeterminate color="primary" />
            </div>

            <div v-else-if="preuves.length === 0" class="text-center text-medium-emphasis py-6">
                ✅ Aucune preuve en attente
            </div>

            <v-card v-else v-for="preuve in preuves" :key="preuve.idInscription" rounded="lg" border elevation="0"
                class="mb-3">
                <v-card-text class="pa-4">
                    <div class="d-flex align-center ga-3">
                        <v-sheet rounded="lg" width="44" height="44"
                            class="d-flex align-center justify-center flex-shrink-0" color="blue-grey-lighten-4">
                            <v-icon color="blue-grey-darken-2">mdi-file-image</v-icon>
                        </v-sheet>
                        <div class="flex-grow-1">
                            <div class="text-body-2 font-weight-semibold">{{ preuve.etudiant }}</div>
                            <div class="text-caption text-medium-emphasis">{{ preuve.action }}</div>
                            <div class="text-caption text-medium-emphasis">{{ preuve.fichier }}</div>
                        </div>
                        <v-chip :color="TYPE_CONFIG[preuve.type]?.color || 'grey'" variant="outlined" size="small" label
                            class="flex-shrink-0">
                            {{ TYPE_CONFIG[preuve.type]?.label || preuve.type }}
                        </v-chip>
                        <div class="d-flex ga-2 flex-shrink-0">
                            <v-btn variant="text" size="small" icon @click="voirFichier(preuve)">
                                <v-icon>mdi-eye</v-icon>
                                <v-tooltip activator="parent">Voir le fichier</v-tooltip>
                            </v-btn>
                            <v-btn color="success" variant="tonal" size="small" rounded="xl"
                                @click="validerPreuve(preuve.idAction, preuve.idInscription)">
                                <v-icon class="mr-1" size="16">mdi-check</v-icon>
                                Valider
                            </v-btn>
                        </div>
                    </div>
                </v-card-text>
            </v-card>
        </div>

        <v-divider class="mb-10" />

        <h1 class="text-h4 font-weight-bold text-center mb-6">Suivi d'activités</h1>

        <div v-if="loading" class="d-flex justify-center py-12">
            <v-progress-circular indeterminate color="primary" />
        </div>

        <template v-else>
            <v-text-field v-model="recherche" placeholder="Recherche" variant="outlined" rounded="xl"
                density="comfortable" prepend-inner-icon="mdi-magnify" clearable hide-details class="mb-6 mx-auto"
                style="max-width: 400px;" />

            <v-list lines="one" rounded="lg" border class="mb-6">
                <template v-if="etudiantsFiltres.length > 0">
                    <v-list-item v-for="etudiant in etudiantsFiltres" :key="etudiant.id"
                        :title="`${etudiant.prenom} ${etudiant.nom}`"
                        :subtitle="`${etudiant.actions.length} action${etudiant.actions.length > 1 ? 's' : ''}`"
                        :active="etudiantSelec?.id === etudiant.id" base-color="bleu" @click="selectionner(etudiant)"
                        style="cursor: pointer;">
                        <template #prepend>
                            <v-avatar color="bleu" size="38">
                                <span class="text-caption font-weight-bold text-white">
                                    {{ etudiant.prenom[0] }}{{ etudiant.nom[0] }}
                                </span>
                            </v-avatar>
                        </template>
                        <template #append>
                            <v-icon>
                                {{ etudiantSelec?.id === etudiant.id ? 'mdi-chevron-up' : 'mdi-chevron-down' }}
                            </v-icon>
                        </template>
                    </v-list-item>
                </template>
                <v-list-item v-else>
                    <v-list-item-title class="text-center text-medium-emphasis py-4">
                        Aucun résultat pour "{{ recherche }}"
                    </v-list-item-title>
                </v-list-item>
            </v-list>

            <v-expand-transition>
                <div v-if="etudiantSelec">
                    <v-card rounded="xl" border elevation="0">
                        <v-card-title class="pa-5 pb-2 d-flex align-center ga-3">
                            <v-avatar color="bleu" size="44">
                                <span class="text-body-2 font-weight-bold text-white">
                                    {{ etudiantSelec.prenom[0] }}{{ etudiantSelec.nom[0] }}
                                </span>
                            </v-avatar>
                            <div>
                                <div class="font-weight-bold text-h6">
                                    {{ etudiantSelec.prenom }} {{ etudiantSelec.nom }}
                                </div>
                                <div class="text-caption text-medium-emphasis">
                                    {{ etudiantSelec.actions.length }} action{{ etudiantSelec.actions.length > 1 ? 's' :
                                        '' }}
                                    réalisée{{ etudiantSelec.actions.length > 1 ? 's' : '' }}
                                </div>
                            </div>
                        </v-card-title>
                        <v-divider class="mx-5" />
                        <v-card-text class="pa-5">
                            <div v-if="etudiantSelec.actions.length === 0"
                                class="text-center text-medium-emphasis py-4">
                                Aucune action réalisée
                            </div>
                            <v-list v-else lines="one" rounded="lg" border>
                                <v-list-item v-for="action in etudiantSelec.actions" :key="action.id"
                                    :title="action.titre" :subtitle="action.date">
                                    <template #prepend>
                                        <v-chip :color="TYPE_CONFIG[action.type]?.color || 'grey'" variant="outlined"
                                            size="small" label class="mr-3"
                                            style="min-width: 130px; justify-content: center;">
                                            {{ TYPE_CONFIG[action.type]?.label || action.type }}
                                        </v-chip>
                                    </template>
                                </v-list-item>
                            </v-list>
                        </v-card-text>
                    </v-card>
                </div>
            </v-expand-transition>
        </template>

    </v-container>

    <v-dialog v-model="previewDialog" max-width="500">
        <v-card v-if="previewFichier" rounded="xl">
            <v-card-title class="pa-5 pb-2 font-weight-bold">
                {{ previewFichier.fichier }}
            </v-card-title>
            <v-card-text class="pa-5 pt-2 text-center">
                <v-sheet rounded="lg" color="grey-lighten-3" height="250" class="d-flex align-center justify-center">
                    <div class="text-center text-medium-emphasis">
                        <v-icon size="48" class="mb-2">mdi-file-image</v-icon>
                        <div class="text-body-2">Aperçu du fichier</div>
                        <div class="text-caption">{{ previewFichier.fichier }}</div>
                    </div>
                </v-sheet>
            </v-card-text>
            <v-card-actions class="pa-5 pt-0">
                <v-spacer />
                <v-btn color="bleu" variant="flat" rounded="xl" @click="previewDialog = false">
                    Fermer
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>