import { createRouter, createWebHistory } from 'vue-router'
import PkIndexView from '../views/pk/PkIndexView.vue'
import RecordIndexView from '../views/record/RecordIndexView.vue'
import RanklistIndexView from '../views/ranklist/RanklistIndexView.vue'
import UserBotIndexView from '../views/user/bots/UserBotIndexView.vue'
import NotFound from '../views/error/NotFound.vue'
import UserAccountLogin from '../views/user/account/UserAccountLoginView.vue'
import UserAccountRegister from '../views/user/account/UserAccountRegisterView.vue'
import store from '../store'

const routes = [
    {
        path: '/',
        name: 'home',
        redirect: '/pk/',
        meta: {
            requestAuth: true,
        }
    },
    {
        path: '/pk/',
        name: 'pk_index',
        component: PkIndexView,
        meta: {
            requestAuth: true,
        }
    },
    {
        path: '/record/',
        name: 'record_index',
        component: RecordIndexView,
        meta: {
            requestAuth: true,
        }
    },
    {
        path: '/ranklist/',
        name: 'ranklist_index',
        component: RanklistIndexView,
        meta: {
            requestAuth: true,
        }
    },
    {
        path: '/user/bots/',
        name: 'user_bot_index',
        component: UserBotIndexView,
        meta: {
            requestAuth: true,
        }
    },
    {
        path: '/user/account/login',
        name: 'user_account_login',
        component: UserAccountLogin,
        meta: {
            requestAuth: false,
        }
    },
    {
        path: '/user/account/register',
        name: 'user_account_register',
        component: UserAccountRegister,
        meta: {
            requestAuth: false,
        }
    },
    {
        path: '/:catchAll(.*)',
        name: '404',
        component: NotFound,
    },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
    if(to.meta.requestAuth && !store.state.user.is_login) {
        next({name: "user_account_login"});
    } else {
        next();
    }
})


export default router
