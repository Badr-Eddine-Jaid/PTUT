<script setup>
import { ref, onMounted, computed } from 'vue' // Ajout de computed
import { useAuth } from '../composables/useAuth.js'

const { authHeaders, estConnecte, estAdmin, token } = useAuth()

const API_BASE = 'https://api-ptut.up.railway.app'

const fichiers = ref([])
const loading = ref(true)
const uploading = ref(false)
const uploadError = ref(null)
const fileInput = ref(null)

// --- FILTRAGE DES RESSOURCES ---
// On crée une liste calculée qui exclut les preuves d'étudiants
const fichiersFiltres = computed(() => {
    return fichiers.value.filter(f => {
        const nom = f.nom.toLowerCase()
        return !nom.includes('preuve') && !nom.includes('justificatif')
    })
})

function formatTaille(bytes) {
    if (!bytes) return ''
    if (bytes < 1024) return `${bytes} o`
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} Ko`
    return `${(bytes / (1024 * 1024)).toFixed(1)} Mo`
}

function typeFromContentType(contentType) {
    if (!contentType) return 'FICHIER'
    if (contentType.includes('pdf')) return 'PDF'
    if (contentType.includes('presentation') || contentType.includes('powerpoint')) return 'PPTX'
    if (contentType.includes('zip')) return 'ZIP'
    if (contentType.includes('word')) return 'DOCX'
    return 'FICHIER'
}

const ICON_COLORS = {
    'PDF': '#E53935',
    'PPTX': '#FB8C00',
    'ZIP': '#8E24AA',
    'DOCX': '#1E88E5',
    'FICHIER': '#546E7A',
}

function iconColor(type) {
    return ICON_COLORS[type] ?? '#546E7A'
}

async function chargerFichiers() {
    try {
        const res = await fetch(`${API_BASE}/resources`, {
            headers: estConnecte.value ? authHeaders() : {}
        })
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        const data = await res.json()
        fichiers.value = data.map(f => ({
            id: f.id,
            nom: f.fileName,
            type: typeFromContentType(f.contentType),
            taille: formatTaille(f.size),
            uploadedBy: f.uploadedBy,
            uploadedAt: f.uploadedAt
        }))
    } catch (e) {
        console.log('Erreur API resources:', e.message)
        fichiers.value = []
    } finally {
        loading.value = false
    }
}

onMounted(chargerFichiers)

async function telecharger(fichier) {
    try {
        const res = await fetch(`${API_BASE}/resources/${fichier.id}/download`, {
            headers: authHeaders()
        })
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        const blob = await res.blob()
        const url = URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = fichier.nom
        a.click()
        URL.revokeObjectURL(url)
    } catch (e) {
        console.log('Erreur téléchargement:', e.message)
    }
}

async function uploaderFichier(event) {
    const file = event.target.files[0]
    if (!file) return

    uploading.value = true
    uploadError.value = null

    try {
        const formData = new FormData()
        formData.append('file', file)

        const res = await fetch(`${API_BASE}/resources/upload`, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${token.value}` },
            body: formData
        })

        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        await chargerFichiers()
    } catch (e) {
        uploadError.value = "Erreur lors de l'upload : " + e.message
    } finally {
        uploading.value = false
        fileInput.value.value = ''
    }
}

async function supprimerFichier(fichier) {
    try {
        const res = await fetch(`${API_BASE}/resources/${fichier.id}`, {
            method: 'DELETE',
            headers: authHeaders()
        })
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        await chargerFichiers()
    } catch (e) {
        console.log('Erreur suppression:', e.message)
    }
}
</script>

<template>
    <v-container class="py-8">

        <div class="d-flex align-center justify-space-between mb-6">
            <h1 class="text-h4 font-weight-bold">Bibliothèque de documents</h1>

            <div v-if="estAdmin">
                <input ref="fileInput" type="file" style="display:none" @change="uploaderFichier" />
                <v-btn color="bleu" @click="fileInput.click()" :loading="uploading" prepend-icon="mdi-upload">
                    Ajouter un fichier
                </v-btn>
            </div>
        </div>

        <v-alert v-if="uploadError" type="error" variant="tonal" class="mb-4" closable
            @click:close="uploadError = null">
            {{ uploadError }}
        </v-alert>

        <div v-if="loading" class="d-flex justify-center py-12">
            <v-progress-circular indeterminate color="primary" />
        </div>

        <template v-else>
            <v-alert v-if="fichiersFiltres.length === 0" type="info" variant="tonal">
                Aucun document disponible pour le moment.
            </v-alert>

            <v-row v-else>
                <v-col v-for="fichier in fichiersFiltres" :key="fichier.id" cols="12" sm="6" md="4" lg="3">
                    <v-card height="80" rounded="lg" color="#F4F6F9" class="pa-3" elevation="0">
                        <div class="d-flex align-center justify-space-between h-100 ga-2">

                            <div class="d-flex align-center overflow-hidden" style="flex: 1; min-width: 0;">
                                <v-sheet rounded="lg" width="44" height="44"
                                    class="d-flex align-center justify-center flex-shrink-0"
                                    :color="iconColor(fichier.type)">
                                    <v-icon color="white" size="22">mdi-file-document</v-icon>
                                </v-sheet>

                                <div class="ml-3 overflow-hidden" style="flex: 1; min-width: 0;">
                                    <div class="text-body-2 font-weight-bold text-truncate" style="line-height: 1.2"
                                        :title="fichier.nom">
                                        {{ fichier.nom }}
                                    </div>
                                    <div class="text-caption text-medium-emphasis">
                                        {{ fichier.type }}<span v-if="fichier.taille"> · {{ fichier.taille }}</span>
                                    </div>
                                </div>
                            </div>

                            <div class="d-flex flex-shrink-0">
                                <v-btn icon variant="text" size="small" @click="telecharger(fichier)">
                                    <v-icon size="20">mdi-download</v-icon>
                                    <v-tooltip activator="parent">Télécharger</v-tooltip>
                                </v-btn>
                                <v-btn v-if="estAdmin" icon variant="text" size="small" color="error"
                                    @click="supprimerFichier(fichier)">
                                    <v-icon size="20">mdi-delete</v-icon>
                                    <v-tooltip activator="parent">Supprimer</v-tooltip>
                                </v-btn>
                            </div>
                        </div>
                    </v-card>
                </v-col>
            </v-row>
        </template>

    </v-container>
</template>

<style scoped>
/* Force le texte à ne pas s'étaler */
.text-truncate {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}
</style>