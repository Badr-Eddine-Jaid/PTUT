<script setup>
import { ref, computed, onMounted } from 'vue'
import Action from '../components/Action.vue'

const TYPES = ['SALON ÉTUDIANT', 'LYCÉE', 'RÉSEAUX SOCIAUX', 'FORMATION']

const MOIS_LABELS = [
    'Janvier', 'Février', 'Mars', 'Avril', 'Mai', 'Juin',
    'Juillet', 'Août', 'Septembre', 'Octobre', 'Novembre', 'Décembre'
]

const MOIS_ABBR = [
    ['janv', 'jan'],
    ['févr', 'fév', 'feb'],
    ['mars', 'mar'],
    ['avr', 'apr'],
    ['mai', 'may'],
    ['juin', 'jun'],
    ['juil', 'jul'],
    ['août', 'aug'],
    ['sept', 'sep'],
    ['oct'],
    ['nov'],
    ['déc', 'dec']
]

const actions = ref([])
const loading = ref(true)
const error = ref(null)
const selectedType = ref('Tous les types')
const selectedMois = ref(null)

function trouverMoisIndex(dateStr) {
    if (!dateStr) return -1
    const low = dateStr.toLowerCase()
    for (let i = 0; i < MOIS_ABBR.length; i++) {
        if (MOIS_ABBR[i].some(abbr => low.includes(abbr))) return i
    }
    return -1
}

onMounted(async () => {
    try {
        const res = await fetch('/api/actions')
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        actions.value = await res.json()
    } catch (e) {
        actions.value = [
            {
                id: 1,
                type: 'SALON ÉTUDIANT',
                titre: 'Salon InfoSup – Toulouse',
                date: '24 janv.',
                lieu: 'Toulouse, Parc des expositions',
                description: 'Tenue du stand ISIS au Parc des Expos.\nPrésentation des filières et distribution de flyers aux prospects.',
                places: 3
            },
            {
                id: 2,
                type: 'LYCÉE',
                titre: 'Lycée Bellevue – Albi',
                date: '24 janv.',
                lieu: 'Albi',
                description: 'Intervention dans les classes de terminale pour présenter les parcours.',
                places: 1
            },
            {
                id: 3,
                type: 'RÉSEAUX SOCIAUX',
                titre: 'Story Instagram – "Vie Campus"',
                date: 'Avant le 24 janv.',
                description: 'Publication d\'une story Instagram mettant en avant la vie sur le campus.',
                lien: 'https://instagram.com'
            },
            {
                id: 4,
                type: 'FORMATION',
                titre: 'Atelier : Pitcher l\'école',
                date: '24 janv.',
                lieu: 'Parc des Expos, Toulouse',
                description: 'Tenue du stand ISIS au Parc des Expos.\nPrésentation des filières et distribution de flyers aux prospects.',
                places: 2
            },
            {
                id: 5,
                type: 'RÉSEAUX SOCIAUX',
                titre: 'Post LinkedIn – Témoignages étudiants',
                date: '15 févr.',
                description: 'Publication de témoignages d\'étudiants en formation.',
                lien: 'https://linkedin.com'
            },
            {
                id: 6,
                type: 'SALON ÉTUDIANT',
                titre: 'Forum des métiers – Montpellier',
                date: '10 mars',
                lieu: 'Montpellier',
                description: 'Participation au forum des métiers de Montpellier.',
                places: 5
            }
        ]
    } finally {
        loading.value = false
    }
})

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
</script>

<template>
    <v-container class="py-8" max-width="860">

        <h1 class="text-h5 font-weight-bold mb-5">Prochaines opportunités</h1>

        <!-- ── Filtres ── -->
        <div class="d-flex ga-3 mb-6 flex-wrap">
            <v-select v-model="selectedType" :items="['Tous les types', ...TYPES]" density="comfortable"
                variant="outlined" rounded="lg" hide-details style="max-width: 220px;" />
            <v-select v-model="selectedMois" :items="moisItems" item-title="label" item-value="value"
                density="comfortable" variant="outlined" rounded="lg" hide-details style="max-width: 200px;" />
        </div>

        <!-- ── Chargement ── -->
        <div v-if="loading" class="d-flex justify-center py-12">
            <v-progress-circular indeterminate color="primary" />
        </div>

        <!-- ── Erreur ── -->
        <v-alert v-else-if="error" type="error" variant="tonal" class="mb-4">
            {{ error }}
        </v-alert>

        <!-- ── Vide ── -->
        <v-alert v-else-if="actionsFiltrees.length === 0" type="info" variant="tonal"
            text="Aucune opportunité pour ces critères." />

        <!-- ── Liste ── -->
        <template v-else>
            <Action v-for="action in actionsFiltrees" :key="action.id" :action="action" />
        </template>

    </v-container>
</template>