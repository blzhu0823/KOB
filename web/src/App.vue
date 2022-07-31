<template>
  <div>
    <div>Bot昵称：{{ bot_name }}</div>
    <div>Bot年龄：{{ bot_age }}</div>
  </div>
  <router-view/>
</template>


<script>

import $ from 'jquery';
import { ref } from 'vue';


export default {
    name: 'App',
    setup() {
        let bot_name = ref("");
        let bot_age = ref("");

        $.ajax({
            url: 'http://localhost:3000/pk/getbotinfo/',
            type: 'get',
            success: function(data) {
                bot_name.value = data.name;
                bot_age.value = data.age;
            }
        });

        return {
            bot_name,
            bot_age
        }
    }
}
</script>


<style>
</style>
