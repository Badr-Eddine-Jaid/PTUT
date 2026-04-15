<script setup>
import { ref, computed, onMounted, watch } from 'vue'
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
const inscriptionsUtilisateur = ref([])
const loading = ref(true)
const error = ref(null)
const selectedType = ref('Tous les types')
const selectedMois = ref(null)

// ── Formattage Date ──
function formatDate(dateStr) {
    if (!dateStr) return ''
    const mois = ['janvier', 'février', 'mars', 'avril', 'mai', 'juin', 'juillet', 'août', 'septembre', 'octobre', 'novembre', 'décembre']
    const parts = dateStr.split('-')
    if (parts.length !== 3) return dateStr
    const moisIdx = parseInt(parts[1]) - 1
    return `${parseInt(parts[2])} ${mois[moisIdx]}`
}

function trouverMoisIndex(dateStr) {
    if (!dateStr) return -1
    // Priorité au format YYYY-MM-DD
    if (dateStr.includes('-')) {
        return parseInt(dateStr.split('-')[1]) - 1
    }
    const low = dateStr.toLowerCase()
    for (let i = 0; i < MOIS_ABBR.length; i++) {
        if (MOIS_ABBR[i].some(abbr => low.includes(abbr))) return i
    }
    return -1
}

function mapAction(a) {
    return {
        id: a.idAction,
        type: a.typeAction,
        titre: a.titre,
        dateAffichage: formatDate(a.dateAction),
        date: a.dateAction,
        lieu: a.lieu,
        description: a.description,
        places: a.capaciteMax, // On utilise capaciteMax comme base dynamique
        typeEtablissement: a.typeEtablissement,
        statut: a.statut
    }
}

// ── Chargement Initial ──
async function chargerDonnees() {
    loading.value = true
    error.value = null
    try {
        const resActions = await fetch(`${API_BASE}/actions`, { headers: authHeaders() })
        const dataActions = await resActions.json()

        let mesInscripIds = []
        if (estConnecte.value && !estAdmin.value) {
            const resInscrip = await fetch(`${API_BASE}/actions/inscriptions/me`, { headers: authHeaders() })
            if (resInscrip.ok) {
                const dataInscrip = await resInscrip.json()
                mesInscripIds = dataInscrip.map(i => i.idAction)
                inscriptionsUtilisateur.value = mesInscripIds
            }
        } else {
            // Réinitialise les inscriptions si déconnecté
            inscriptionsUtilisateur.value = []
        }

        actions.value = dataActions.map(a => {
            const baseAction = mapAction(a)
            if (mesInscripIds.includes(baseAction.id)) {
                baseAction.places = Math.max(0, baseAction.places - 1)
            }
            return baseAction
        })

    } catch (e) {
        error.value = 'Erreur chargement'
    } finally {
        loading.value = false
    }
}

onMounted(chargerDonnees)

// ── Rechargement automatique à la connexion / déconnexion ──
watch(estConnecte, () => {
    chargerDonnees()
})

// ── GESTION DE L'INSCRIPTION (Mise à jour du compteur) ──
function gererInscriptionReussie(idAction) {
    const index = actions.value.findIndex(a => a.id === idAction)
    if (index !== -1) {
        // On remplace l'objet entier pour forcer la réactivité de Vue
        const actionMaj = { ...actions.value[index] }

        if (actionMaj.places > 0) {
            actionMaj.places-- // On enlève une place localement
        }

        actions.value[index] = actionMaj

        // On l'ajoute à la liste des "déjà inscrit" pour griser le bouton
        if (!inscriptionsUtilisateur.value.includes(idAction)) {
            inscriptionsUtilisateur.value.push(idAction)
        }
    }
}

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

// ── Admin Form ──
const dialogAction = ref(false)
const modeEdition = ref(false)
const actionEnCours = ref({ titre: '', type: 'FORMATION', date: '', lieu: '', description: '', places: null })
const sauvegardeEnCours = ref(false)

function ouvrirAjout() {
    modeEdition.value = false
    actionEnCours.value = { titre: '', type: 'FORMATION', date: '', lieu: '', description: '', places: null }
    dialogAction.value = true
}

function ouvrirModification(action) {
    modeEdition.value = true
    actionEnCours.value = { ...action }
    dialogAction.value = true
}

async function sauvegarder() {
    sauvegardeEnCours.value = true
    try {
        const method = modeEdition.value ? 'PUT' : 'POST'
        const url = modeEdition.value ? `${API_BASE}/actions/${actionEnCours.value.id}` : `${API_BASE}/actions`
        const res = await fetch(url, {
            method,
            headers: authHeaders(),
            body: JSON.stringify({
                titre: actionEnCours.value.titre,
                typeAction: actionEnCours.value.type,
                dateAction: actionEnCours.value.date,
                lieu: actionEnCours.value.lieu,
                description: actionEnCours.value.description,
                capaciteMax: actionEnCours.value.places
            })
        })
        if (!res.ok) throw new Error('Erreur sauvegarde')
        dialogAction.value = false
        await chargerDonnees()
    } catch (e) {
        alert(e.message)
    } finally {
        sauvegardeEnCours.value = false
    }
}
</script>

<template>
    <v-container class="py-8" max-width="860">
        <div class="d-flex align-center mb-5">
            <h1 class="text-h5 font-weight-bold">Prochaines opportunités</h1>
            <v-spacer />
            <v-btn v-if="estAdmin" color="bleu" variant="flat" rounded="xl" @click="ouvrirAjout">
                <v-icon class="mr-2">mdi-plus</v-icon> Ajouter
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

        <template v-else>
            <Action v-for="action in actionsFiltrees" :key="action.id" :action="action"
                :dejaInscrit="inscriptionsUtilisateur.includes(action.id)" @modifier="ouvrirModification"
                @inscriptionReussie="gererInscriptionReussie" />
        </template>
    </v-container>

    <v-dialog v-model="dialogAction" max-width="560">
        <v-card rounded="xl" class="pa-4">
            <v-card-title>{{ modeEdition ? 'Modifier' : 'Ajouter' }}</v-card-title>
            <v-card-text>
                <v-select v-model="actionEnCours.type" :items="TYPES" label="Type" variant="outlined" class="mb-3" />
                <v-text-field v-model="actionEnCours.titre" label="Titre" variant="outlined" class="mb-3" />
                <v-text-field v-model="actionEnCours.date" label="Date (AAAA-MM-JJ)" variant="outlined" class="mb-3" />
                <v-text-field v-model="actionEnCours.lieu" label="Lieu" variant="outlined" class="mb-3" />
                <v-textarea v-model="actionEnCours.description" label="Description" variant="outlined" rows="3" />
                <v-text-field v-model.number="actionEnCours.places" label="Places" type="number" variant="outlined" />
            </v-card-text>
            <v-card-actions>
                <v-btn variant="text" @click="dialogAction = false">Annuler</v-btn>
                <v-spacer />
                <v-btn color="bleu" variant="flat" :loading="sauvegardeEnCours" @click="sauvegarder">Enregistrer</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>