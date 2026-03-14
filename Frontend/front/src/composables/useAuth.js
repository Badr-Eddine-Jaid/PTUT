import { ref, computed } from 'vue'

// ── Faux comptes pour tester (à remplacer par l'API plus tard) ──
const FAUX_COMPTES = [
    { identifiant: 'admin', motDePasse: 'Admin123!', nom: 'Service Com', role: 'admin' },
    { identifiant: 'csalva', motDePasse: 'test', prenom: 'Clément', nom: 'SALVA', role: 'etudiant' },
]

// ── État global ──
const utilisateur = ref(null)

const estConnecte = computed(() => utilisateur.value !== null)
const estAdmin = computed(() => utilisateur.value?.role === 'admin')

// ── Connexion ──
function login(identifiant, motDePasse) {
    const compte = FAUX_COMPTES.find(
        c => c.identifiant === identifiant && c.motDePasse === motDePasse
    )

    if (!compte) throw new Error('Identifiant ou mot de passe incorrect')

    utilisateur.value = {
        prenom: compte.prenom,
        nom: compte.nom,
        role: compte.role
    }
}

// ── Déconnexion ──
function logout() {
    utilisateur.value = null
}

export function useAuth() {
    return { utilisateur, estConnecte, estAdmin, login, logout }
}