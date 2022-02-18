import Vue from 'vue'
import Vuex from 'vuex'
Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    token: localStorage.getItem('token') || null,
    userDetails: localStorage.getItem('userDetails') || null,
    librarianLocation: localStorage.getItem('librarianLocation') || null,
  },
  mutations: {
    login(state, data){
      localStorage.setItem('token', data.token)
      localStorage.setItem('userDetails', data.username)
      state.user = data.username
      state.token = data.token
    },
    logout(state) {
      // 移除token
      localStorage.removeItem('token')
      localStorage.removeItem('userDetails')
      localStorage.removeItem('librarianLocation')
      localStorage.removeItem('authority')
      state.userDetails = null
      state.token = null
      state.librarianLocation = null
    }
  },
  actions: {
  }
})
