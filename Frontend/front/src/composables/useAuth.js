import { ref, computed } from 'vue'

const API_BASE = 'https://api-ptut.up.railway.app'

const token = ref(localStorage.getItem('token') || null)
const utilisateur = ref(JSON.parse(localStorage.getItem('utilisateur') || 'null'))

const estConnecte = computed(() => utilisateur.value !== null)
const estAdmin = computed(() => utilisateur.value?.role === 'ADMIN')
const estEtudiant = computed(() => utilisateur.value?.role === 'AMBASSADEUR')

async function login(email, password) {
    const res = await fetch(`${API_BASE}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
    })

    if (!res.ok) {
        const err = await res.json().catch(() => ({}))
        throw new Error(err.message || 'Identifiant ou mot de passe incorrect')
    }

    const data = await res.json()
    token.value = data.token
    utilisateur.value = {
        email: data.email,
        role: data.role,
        prenom: data.prenom ?? data.email,
        nom: data.nom ?? ''
    }

    localStorage.setItem('token', token.value)
    localStorage.setItem('utilisateur', JSON.stringify(utilisateur.value))

    return utilisateur.value
}

async function fetchMe() {
    if (!token.value) return

    const res = await fetch(`${API_BASE}/auth/me`, {
        headers: { 'Authorization': `Bearer ${token.value}` }
    })

    if (!res.ok) {
        logout()
        return
    }

    const data = await res.json()
    utilisateur.value = {
        ...utilisateur.value,
        prenom: data.prenom ?? utilisateur.value.prenom,
        nom: data.nom ?? utilisateur.value.nom,
        email: data.email ?? utilisateur.value.email,
        role: data.role ?? utilisateur.value.role,
    }

    localStorage.setItem('utilisateur', JSON.stringify(utilisateur.value))
}

function logout() {
    utilisateur.value = null
    token.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('utilisateur')
}

function authHeaders() {
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token.value}`
    }
}

export function useAuth() {
    return {
        utilisateur,
        token,
        estConnecte,
        estAdmin,
        estEtudiant,
        login,
        fetchMe,
        logout,
        authHeaders
    }
}