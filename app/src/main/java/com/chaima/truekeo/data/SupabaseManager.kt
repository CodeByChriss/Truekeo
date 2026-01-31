package com.chaima.truekeo.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage

object SupabaseClient {
    val supabase = createSupabaseClient(
        supabaseUrl = "https://xcawesphifjagaixywdh.supabase.co",
        supabaseKey = "sb_publishable_pih72vXtMr3MZX0giDtkBw_s253mFOD"
    ) {
        install(Storage)
    }
}