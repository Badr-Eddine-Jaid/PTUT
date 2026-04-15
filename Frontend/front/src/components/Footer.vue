<script setup>
import { useDisplay } from 'vuetify'

const { smAndDown } = useDisplay()
</script>

<template>
    <v-footer app color="#f5f5f5" class="border-top pa-2" style="border-top: 1px solid #e0e0e0;">
        <v-container fluid class="pa-0">
            <div class="d-flex align-center justify-center w-100" :class="smAndDown ? 'flex-column ga-2' : 'flex-row'">
                <div class="d-flex align-center">
                    <img src="../assets/logo-inuc.png" height="25px" class="mx-2" />
                    <img src="../assets/logo-insa.gif" height="40px" class="mx-2" />
                </div>

                <v-divider v-if="!smAndDown" vertical class="mx-4 footer-divider" />
                <v-divider v-else class="w-50 mx-auto footer-divider" />

                <div class="footer-text">
                    © ISIS 2026 • Tous droits réservés
                </div>

                <v-divider v-if="!smAndDown" vertical class="mx-4 footer-divider" />

                <div class="d-flex align-center justify-center ga-1">
                    <v-btn variant="text" size="small" class="footer-link">Mentions Légales</v-btn>
                    <span v-if="!smAndDown" class="footer-text">•</span>
                    <v-btn variant="text" size="small" class="footer-link">Support</v-btn>
                </div>
            </div>
        </v-container>
    </v-footer>
</template>

<style scoped>
/* On garde tes styles mais on s'assure qu'ils ne forcent pas de largeur */
.footer-text {
    color: #888;
    font-size: 11px;
    /* Légèrement plus petit pour le mobile */
    white-space: nowrap;
}

.footer-divider {
    border-color: #e0e0e0 !important;
    opacity: 0.6;
}

.footer-link {
    color: #888 !important;
    font-size: 11px !important;
    text-transform: none;
    min-width: unset;
    padding: 0 4px;
}

.footer-link:hover {
    color: #8B4FC8 !important;
}

.v-footer {
    position: relative !important;
}
</style>