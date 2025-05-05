package com.umn.story.utils

import android.content.Context


class SharedPrefUtils {
    companion object{
        public fun saveNim(nim:String, ctx: Context){
            val sharedPreferences = ctx.getSharedPreferences("users", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("nim", nim)
            editor.apply()
        }
        public fun removeNim(ctx:Context){
            val sharedPreferences = ctx.getSharedPreferences("users", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("nim")
            editor.apply()
        }
        public fun getNim(ctx:Context):String?{
            val sharedPreferences = ctx.getSharedPreferences("users", Context.MODE_PRIVATE)
            return sharedPreferences.getString("nim",null)
        }
    }
}