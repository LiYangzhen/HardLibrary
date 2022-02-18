// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import {router} from './router'
import store from './store'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

Vue.use(ElementUI)

// axios 配置
let axios = require('axios')
// Axios挂载到prototype，全局可以使用this.$axios访问
Vue.prototype.$axios = axios
axios.defaults.baseURL = '/api'
axios.defaults.withCredentials = true
axios.defaults.headers.post['Content-Type'] = 'application/json;charset=UTF-8'

let protocol = window.location.protocol
let host = window.location.host
let reg = /^localhost+/
if (reg.test(host)){

} else {
  axios.defaults.baseURL = protocol + "//" + host + "/api"
}

Vue.config.productionTip = false

// http request 拦截器
axios.interceptors.request.use(
  config => {
    if (store.state.token) {
      // 判断是否有token，若存在，每个http header加上token
      config.headers.token = store.state.token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// http response 拦截器
axios.interceptors.response.use(
  response => {
    return response
  },
  error => {
    if (error.response.data.message === 'Credentials insufficient') {
      // 清除token 如果不是register/login, 跳转至login
      store.commit('logout')
      router.currentRoute.path !== '/login' &&
      router.currentRoute.path !== '/register' &&
      router.currentRoute.path !== '/browse' &&
      router.replace({
        path: '/login',
        query: {redirect: router.currentRoute.path}
      })
    }
      return Promise.reject(error)
  }
)

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  components: {App},
  template: '<App/>'
})
