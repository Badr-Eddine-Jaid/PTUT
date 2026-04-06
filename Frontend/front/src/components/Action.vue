<script setup>
import { ref, computed } from 'vue'
import { useAuth } from '../composables/useAuth.js'
import { useDisplay } from 'vuetify' // 🎯 Import pour le responsive

const { estConnecte, estAdmin, authHeaders } = useAuth()
const { smAndDown } = useDisplay() // 🎯 Vrai si l'écran est petit (mobile)

const props = defineProps({
    action: { type: Object, required: true },
    dejaInscrit: { type: Boolean, default: false }
})

const emit = defineEmits(['modifier', 'supprimer', 'inscriptionReussie'])

const isOpen = ref(false)
const inscriptionEnCours = ref(false)
const erreurInscription = ref('')

const TYPE_CONFIG = {
    'SALON ÉTUDIANT': { border: 'salon', fond: 'salon_fond' },
    'LYCÉE': { border: 'lycee', fond: 'lycee_fond' },
    'RÉSEAUX SOCIAUX': { border: 'reseaux', fond: 'reseaux_fond' },
    'FORMATION': { border: 'formation', fond: 'formation_fond' }
}

const badgeConfig = computed(() => TYPE_CONFIG[props.action.type] ?? { border: 'grey', fond: 'grey-lighten-3' })

async function sInscrire() {
    if (!estConnecte.value) {
        erreurInscription.value = 'Connectez-vous pour vous inscrire'
        return
    }
    inscriptionEnCours.value = true
    erreurInscription.value = ''
    try {
        const res = await fetch(`${API_BASE}/actions/${props.action.id}/inscriptions`, {
            method: 'POST',
            headers: authHeaders()
        })
        if (!res.ok) {
            const err = await res.json().catch(() => ({}))
            throw new Error(err.message || "Erreur lors de l'inscription")
        }
        emit('inscriptionReussie', props.action.id)
    } catch (e) {
        erreurInscription.value = e.message
    } finally {
        inscriptionEnCours.value = false
    }
}
</script>

<template>
    <v-card class="mb-3" rounded="lg" border elevation="0">
        <v-card-text class="action-header pa-4" @click="isOpen = !isOpen" style="cursor: pointer;">
            <div class="d-flex flex-wrap align-center ga-3">

                <v-chip :color="badgeConfig.border"
                    :style="`background-color: rgb(var(--v-theme-${badgeConfig.fond}));`" variant="outlined"
                    :size="smAndDown ? 'small' : 'large'" rounded="xl" class="badge-chip font-weight-bold flex-shrink-0"
                    label>
                    {{ action.type }}
                </v-chip>

                <span class="action-title flex-grow-1 text-body-2 text-sm-body-1 font-weight-semibold"
                    :class="smAndDown ? 'w-100 order-3' : ''">
                    {{ action.titre }}
                </span>

                <span class="text-caption text-sm-body-2 text-medium-emphasis text-no-wrap ml-auto"
                    :class="smAndDown ? 'order-2' : ''">
                    {{ action.dateAffichage || action.date }}
                </span>

                <div v-if="estAdmin" class="d-flex ga-1 order-4">
                    <v-btn icon variant="text" size="x-small" @click.stop="emit('modifier', action)">
                        <v-icon size="16">mdi-pencil</v-icon>
                    </v-btn>
                    <v-btn icon variant="text" size="x-small" color="error" @click.stop="emit('supprimer', action)">
                        <v-icon size="16">mdi-delete</v-icon>
                    </v-btn>
                </div>

                <v-icon :icon="isOpen ? 'mdi-chevron-up' : 'mdi-chevron-down'" color="medium-emphasis"
                    class="order-5" />
            </div>
        </v-card-text>

        <v-expand-transition>
            <div v-if="isOpen">
                <v-divider />
                <v-card-text class="px-5 pb-5 pt-4">
                    <div class="d-flex" :class="smAndDown ? 'flex-column ga-4' : 'align-center ga-6'">

                        <div class="flex-grow-1" style="min-width: 0;">
                            <div v-if="action.description" class="mb-4">
                                <p class="text-caption font-weight-black text-uppercase letter-spacing mb-1">Description
                                </p>
                                <p class="text-body-2 text-medium-emphasis" style="white-space: pre-line;">{{
                                    action.description }}</p>
                            </div>
                            <p v-if="action.lieu" class="text-body-2 mb-1"><strong>Lieu :</strong> {{ action.lieu }}</p>
                            <p v-if="action.responsable" class="text-body-2 mb-0"><strong>Responsable :</strong> {{
                                action.responsable }}</p>

                            <v-alert v-if="erreurInscription" type="error" variant="tonal" density="compact"
                                class="mt-3">
                                {{ erreurInscription }}
                            </v-alert>
                        </div>

                        <div v-if="!estAdmin" class="d-flex flex-column ga-3 flex-shrink-0"
                            :style="smAndDown ? 'width: 100%' : 'width: 180px; align-items: flex-end;'">

                            <v-chip v-if="action.places !== undefined" :color="action.places > 0 ? 'success' : 'error'"
                                variant="tonal" size="small"
                                class="font-weight-medium align-self-center align-self-sm-end">
                                {{ action.places }} place{{ action.places > 1 ? 's' : '' }} restante{{ action.places > 1
                                ? 's' : '' }}
                            </v-chip>

                            <v-btn :color="dejaInscrit ? 'grey-darken-1' : 'bleu'" variant="flat"
                                :size="smAndDown ? 'default' : 'large'" rounded="xl" :loading="inscriptionEnCours"
                                :disabled="dejaInscrit || action.places <= 0" class="font-weight-bold"
                                @click.stop="sInscrire" block>
                                <v-icon start size="18">{{ dejaInscrit ? 'mdi-check-circle' : 'mdi-send' }}</v-icon>
                                {{ dejaInscrit ? 'Déjà inscrit' : "S'inscrire" }}
                            </v-btn>
                        </div>
                    </div>
                </v-card-text>
            </div>
        </v-expand-transition>
    </v-card>
</template>

<style scoped>
.action-header:hover {
    background-color: rgba(0, 0, 0, 0.02);
}

.badge-chip {
    min-width: 130px;
    /* Un peu réduit pour le mobile */
    justify-content: center;
}

/* Sur mobile, on permet au titre de prendre toute la largeur sous le badge */
@media (max-width: 600px) {
    .badge-chip {
        min-width: 110px;
    }

    .order-2 {
        order: 2;
    }

    .order-3 {
        order: 3;
    }
}

.action-title {
    color: #1a1a2e;
    line-height: 1.2;
}

.letter-spacing {
    letter-spacing: 0.08em;
}
</style>