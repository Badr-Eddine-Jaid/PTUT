import { createRouter, createWebHistory } from 'vue-router'
import Catalogue from '../views/Catalogue.vue'
import Ressources from '../views/Ressources.vue'

const router = createRouter({
    history: createWebHistory(),
    routes: [
        { path: '/catalogue', name: 'catalogue', component: Catalogue },
        { path: '/ressources', name: 'ressources', component: Ressources },
        { path: '/', redirect: { name: 'catalogue' } },
        { path: '/statistiques', name: 'statistiques', component: () => import('../views/Statistiques.vue') },
        { path: '/actions', name: 'actions', component: () => import('../views/Actions.vue') },
        { path: '/mes-actions', name: 'mes-actions', component: () => import('../views/MesActions.vue') }
    ]
})

export default router