<script setup>
import { ref, computed, onMounted } from 'vue'
import Action from '../components/Action.vue'
import { useAuth } from '../composables/useAuth.js'

const { authHeaders, estConnecte, estAdmin } = useAuth()

const API_BASE = 'https://api-ptut.up.railway.app'

const TYPES = ['SALON ÉTUDIANT', 'LYCÉE', 'RÉSEAUX SOCIAUX', 'FORMATION']

const MOIS_LABELS = [
    'Janvier', 'Février', 'Mars', 'Avril', 'Mai', 'Juin',
    'Juillet', 'Août', 'Septembre', 'Octobre', 'Novembre', 'Décembre'
]

const MOIS_ABBR = [
    ['janv', 'jan'], ['févr', 'fév', 'feb'], ['mars', 'mar'],
    ['avr', 'apr'], ['mai', 'may'], ['juin', 'jun'],
    ['juil', 'jul'], ['août', 'aug'], ['sept', 'sep'],
    ['oct'], ['nov'], ['déc', 'dec']
]

const actions = ref([])
const loading = ref(true)
const error = ref(null)
const selectedType = ref('Tous les types')
const selectedMois = ref(null)

// ── Convertit "2026-01-24" en "24 janvier" ──
function formatDate(dateStr) {
    if (!dateStr) return ''
    const mois = [
        'janvier', 'février', 'mars', 'avril', 'mai', 'juin',
        'juillet', 'août', 'septembre', 'octobre', 'novembre', 'décembre'
    ]
    const parts = dateStr.split('-')
    if (parts.length !== 3) return dateStr
    const jour = parseInt(parts[2])
    const moisIdx = parseInt(parts[1]) - 1
    if (isNaN(jour) || isNaN(moisIdx)) return dateStr
    return `${jour} ${mois[moisIdx]}`
}

function trouverMoisIndex(dateStr) {
    if (!dateStr) return -1
    const low = dateStr.toLowerCase()
    for (let i = 0; i < MOIS_ABBR.length; i++) {
        if (MOIS_ABBR[i].some(abbr => low.includes(abbr))) return i
    }
    return -1
}

// ── Mapper les champs API → format front ──
function mapAction(a) {
    return {
        id: a.idAction,
        type: a.typeAction,
        titre: a.titre,
        dateAffichage: formatDate(a.dateAction), // Pour les cartes
        date: a.dateAction,
        lieu: a.lieu,
        description: a.description,
        places: a.capaciteMax,
        typeEtablissement: a.typeEtablissement,
        statut: a.statut
    }
}

// ── Formulaire action ──
const actionVide = () => ({
    titre: '', type: 'FORMATION', date: '', lieu: '',
    description: '', places: null, typeEtablissement: '', statut: 'OUVERT'
})

const dialogAction = ref(false)
const modeEdition = ref(false)
const actionEnCours = ref(actionVide())
const sauvegardeEnCours = ref(false)
const erreurForm = ref('')

// ── Dialog suppression ──
const dialogSupprimer = ref(false)
const actionASupprimer = ref(null)
const suppressionEnCours = ref(false)

// ── Chargement ──
async function chargerActions() {
    loading.value = true
    error.value = null
    try {
        const res = await fetch(`${API_BASE}/actions`, {
            headers: estConnecte.value ? authHeaders() : {}
        })
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        const data = await res.json()
        actions.value = data.map(mapAction)
    } catch (e) {
        console.log('Erreur API actions:', e.message)
        error.value = 'Impossible de charger les actions. Veuillez réessayer.'
    } finally {
        loading.value = false
    }
}

onMounted(chargerActions)

// ── Filtres ──
const moisItems = computed(() => {
    const set = new Set()
    actions.value.forEach(a => {
        const i = trouverMoisIndex(a.date)
        if (i !== -1) set.add(i)
    })
    const list = [...set].sort((a, b) => a - b).map(i => ({ value: i, label: MOIS_LABELS[i] }))
    return [{ value: null, label: 'Tous les mois' }, ...list]
})

const actionsFiltrees = computed(() => {
    return actions.value.filter(a => {
        const okType = selectedType.value === 'Tous les types' || a.type === selectedType.value
        const okMois = selectedMois.value === null || trouverMoisIndex(a.date) === selectedMois.value
        return okType && okMois
    })
})

// ── Ajout ──
function ouvrirAjout() {
    modeEdition.value = false
    actionEnCours.value = actionVide()
    erreurForm.value = ''
    dialogAction.value = true
}

// ── Modification ──
function ouvrirModification(action) {
    modeEdition.value = true
    actionEnCours.value = { ...action }
    erreurForm.value = ''
    dialogAction.value = true
}

// ── Sauvegarder ──
async function sauvegarder() {
    erreurForm.value = ''
    sauvegardeEnCours.value = true
    try {
        const method = modeEdition.value ? 'PUT' : 'POST'
        const url = modeEdition.value
            ? `${API_BASE}/actions/${actionEnCours.value.id}`
            : `${API_BASE}/actions`

        const res = await fetch(url, {
            method,
            headers: authHeaders(),
            body: JSON.stringify({
                titre: actionEnCours.value.titre,
                typeAction: actionEnCours.value.type,
                dateAction: actionEnCours.value.date,
                lieu: actionEnCours.value.lieu,
                description: actionEnCours.value.description,
                capaciteMax: actionEnCours.value.places,
                typeEtablissement: actionEnCours.value.typeEtablissement ?? '',
                statut: actionEnCours.value.statut ?? 'OUVERT'
            })
        })
        if (!res.ok) {
            const err = await res.json().catch(() => ({}))
            throw new Error(err.message || 'Erreur lors de la sauvegarde')
        }
        dialogAction.value = false
        await chargerActions()
    } catch (e) {
        erreurForm.value = e.message
    } finally {
        sauvegardeEnCours.value = false
    }
}

// ── Suppression ──
function confirmerSuppression(action) {
    actionASupprimer.value = action
    dialogSupprimer.value = true
}

async function supprimer() {
    suppressionEnCours.value = true
    try {
        const res = await fetch(`${API_BASE}/actions/${actionASupprimer.value.id}`, {
            method: 'DELETE',
            headers: authHeaders()
        })
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        dialogSupprimer.value = false
        actionASupprimer.value = null
        await chargerActions()
    } catch (e) {
        console.log('Erreur suppression:', e.message)
    } finally {
        suppressionEnCours.value = false
    }
}
</script>

<template>
    <v-container class="py-8" max-width="860">

        <div class="d-flex align-center mb-5">
            <h1 class="text-h5 font-weight-bold">Prochaines opportunités</h1>
            <v-spacer />
            <v-btn v-if="estAdmin" color="bleu" variant="flat" rounded="xl" @click="ouvrirAjout">
                <v-icon class="mr-2">mdi-plus</v-icon>
                Ajouter une action
            </v-btn>
        </div>

        <div class="d-flex ga-3 mb-6 flex-wrap">
            <v-select v-model="selectedType" :items="['Tous les types', ...TYPES]" density="comfortable"
                variant="outlined" rounded="lg" hide-details style="max-width: 220px;" />
            <v-select v-model="selectedMois" :items="moisItems" item-title="label" item-value="value"
                density="comfortable" variant="outlined" rounded="lg" hide-details style="max-width: 200px;" />
        </div>

        <div v-if="loading" class="d-flex justify-center py-12">
            <v-progress-circular indeterminate color="primary" />
        </div>
        <v-alert v-else-if="error" type="error" variant="tonal" class="mb-4">{{ error }}</v-alert>
        <v-alert v-else-if="actionsFiltrees.length === 0" type="info" variant="tonal"
            text="Aucune opportunité pour ces critères." />

        <template v-else>
            <Action v-for="action in actionsFiltrees" :key="action.id" :action="action" @modifier="ouvrirModification"
                @supprimer="confirmerSuppression" />
        </template>

    </v-container>

    <!-- ── Dialog Ajout / Modification ── -->
    <v-dialog v-model="dialogAction" max-width="560" persistent>
        <v-card rounded="xl">
            <v-card-title class="pa-5 pb-2 font-weight-bold text-h6">
                {{ modeEdition ? "Modifier l'action" : 'Ajouter une action' }}
            </v-card-title>
            <v-card-text class="pa-5 pt-2">
                <v-alert v-if="erreurForm" type="error" variant="tonal" density="compact" class="mb-4">
                    {{ erreurForm }}
                </v-alert>
                <v-select v-model="actionEnCours.type" :items="TYPES" label="Type" variant="outlined" rounded="lg"
                    density="comfortable" class="mb-3" hide-details />
                <v-text-field v-model="actionEnCours.titre" label="Titre" variant="outlined" rounded="lg"
                    density="comfortable" class="mb-3" hide-details />
                <v-text-field v-model="actionEnCours.date" label="Date" variant="outlined" rounded="lg"
                    density="comfortable" class="mb-3" hide-details />
                <v-text-field v-model="actionEnCours.lieu" label="Lieu" variant="outlined" rounded="lg"
                    density="comfortable" class="mb-3" hide-details />
                <v-textarea v-model="actionEnCours.description" label="Description" variant="outlined" rounded="lg"
                    density="comfortable" rows="3" class="mb-3" hide-details />
                <v-text-field v-model.number="actionEnCours.places" label="Nombre de places" type="number"
                    variant="outlined" rounded="lg" density="comfortable" hide-details />
            </v-card-text>
            <v-card-actions class="pa-5 pt-0 d-flex ga-2">
                <v-btn variant="text" rounded="xl" @click="dialogAction = false">Annuler</v-btn>
                <v-spacer />
                <v-btn color="bleu" variant="flat" rounded="xl" :loading="sauvegardeEnCours"
                    :disabled="!actionEnCours.titre || !actionEnCours.type" @click="sauvegarder">
                    {{ modeEdition ? 'Enregistrer' : 'Ajouter' }}
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>

    <!-- ── Dialog Suppression ── -->
    <v-dialog v-model="dialogSupprimer" max-width="400">
        <v-card rounded="xl">
            <v-card-title class="pa-5 pb-2 font-weight-bold text-h6">Supprimer l'action ?</v-card-title>
            <v-card-text class="pa-5 pt-2 text-medium-emphasis">
                Voulez-vous vraiment supprimer <strong>{{ actionASupprimer?.titre }}</strong> ?
                Cette action est irréversible.
            </v-card-text>
            <v-card-actions class="pa-5 pt-0 d-flex ga-2">
                <v-btn variant="text" rounded="xl" @click="dialogSupprimer = false">Annuler</v-btn>
                <v-spacer />
                <v-btn color="error" variant="flat" rounded="xl" :loading="suppressionEnCours" @click="supprimer">
                    Supprimer
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>