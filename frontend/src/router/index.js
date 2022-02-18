import Vue from 'vue'
import Router from 'vue-router'
import Login from '../components/common/login'
import Register from '../components/common/register'
import Home from '../components/common/Home'
import store from '../store'
import SearchResult from '../components/common/searchResult'
import Upload from '../components/admin/upload'
import Browse from '../components/common/browse'
import Details from '../components/common/details'
import ChangePassword from '../components/common/changePassword'
import BorrowBook from '../components/admin/borrowBook'
import ReturnBook from '../components/admin/returnBook'
import SystemSettings from '../components/superAdmin/systemSettings'
import GetReservedBook from '../components/admin/getReservedBook'
import UserDetails from '../components/common/userDetails'
import HistoryManagement from '../components/admin/historyManagement'
import Ratings from '../components/user/ratings'
import RatingsManagement from '../components/admin/ratingsManagement'
import CommentsDetails from '../components/common/commentsDetails'

Vue.use(Router)

export const router = new Router({
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: Login
    },
    {
      path: '/register',
      name: 'Register',
      component: Register
    },
    {
      path: '/home',
      name: 'Home',
      component: Home,
      children: [
        {
          path: '/searchResult',
          name: 'SearchResult',
          component: SearchResult
        },
        {
          path: '/upload',
          name: 'Upload',
          component: Upload,
          meta: {
            requireAuth: true
          }
        },
        {
          path: '/browse',
          name: 'Browse',
          component: Browse
        },
        {
          path: '/details',
          name: 'Details',
          component: Details
        },
        {
          path: '/changePassword',
          name: 'ChangePassword',
          component: ChangePassword,
          meta: {
            requireAuth: true
          }
        },
        {
          path: '/ratingsManagement',
          name: 'RatingsManagement',
          component: RatingsManagement,
          meta: {
            requireAuth: true
          }
        },
        {
          path: '/ratings',
          name: 'Ratings',
          component: Ratings,
          meta: {
            requireAuth: true
          }
        },
        {
          path: '/commentsDetails',
          name: 'CommentsDetails',
          component: CommentsDetails,
          meta: {
            requireAuth: true
          }
        },
        {
          path: '/borrowBook',
          name: 'BorrowBook',
          component: BorrowBook,
          meta: {
            requireAuth: true
          }
        },
        {
          path: '/returnBook',
          name: 'returnBook',
          component: ReturnBook,
          meta: {
            requireAuth: true
          }
        },
        {
          path: '/systemSettings',
          name: 'SystemSettings',
          component: SystemSettings,
          meta: {
            requireAuth: true
          }
        },
        {
          path: '/getReservedBook',
          name: 'GetReservedBook',
          component: GetReservedBook,
          meta: {
            requireAuth: true
          }
        },
        {
          path: '/userDetails',
          name: 'UserDetails',
          component: UserDetails,
          meta: {
            requireAuth: true
          }
        },
        {
          path: '/historyManagement',
          name: 'HistoryManagement',
          component: HistoryManagement,
          meta: {
            requireAuth: true
          }
        },
      ]
    },
    {
      path: '/',
      redirect: '/browse'
    }
  ]
})

// 前端登录拦截
router.beforeEach(function (to, from, next) {
  if (to.matched.some(record => record.meta.requireAuth)) {
    if (store.state.token) {
      next()
    } else {
      next({
        path: '/login',
        query: {redirect: to.fullPath, info: 'Please log in'} // 将目标路由保存，登录后跳转到该路由，并给出提示信息
      })
    }
  } else {
    next()
  }
})
