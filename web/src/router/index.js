import { createRouter, createWebHistory } from 'vue-router'
import PkIndexView from '../views/pk/PkIndexView.vue'
import RecordIndexView from '../views/record/RecordIndexView.vue'
import RanklistIndexView from '../views/ranklist/RanklistIndexView.vue'
import UserBotIndexView from '../views/user/bots/UserBotIndexView.vue'
import NotFound from '../views/error/NotFound.vue'

const routes = [
    {
        path: '/',
        name: 'home',
    },
    {
        path: '/pk/',
        name: 'pk_index',
        component: PkIndexView,
    },
    {
        path: '/record/',
        name: 'record_index',
        component: RecordIndexView,
    },
    {
        path: '/ranklist/',
        name: 'ranklist_index',
        component: RanklistIndexView,
    },
    {
        path: '/user/bots/',
        name: 'user_bot_index',
        component: UserBotIndexView,
    },
    {
        path: '/404/',
        name: '404',
        component: NotFound,
    },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
