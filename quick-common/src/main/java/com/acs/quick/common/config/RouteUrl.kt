/*
 * Copyright 2026 zhaijie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.acs.quick.common.config

/**
 * TheRouter 路由常量。
 */
object RouteUrl {

    // Main
    object Main {
        const val GROUP_MAIN = "/main"
        const val PAGE_SPLASH_ACTIVITY = "\${GROUP_MAIN}/SplashActivity"
        const val PAGE_LOGIN_ACTIVITY = "\${GROUP_MAIN}/LoginActivity"
        const val PAGE_MAIN_ACTIVITY = "\${GROUP_MAIN}/MainActivity"
    }

    // Home
    object Home {
        const val GROUP_HOME = "/home"
        const val PAGE_HOME_FRAGMENT = "\${GROUP_HOME}/HomeFragment"
    }

    // Office
    object Office {
        const val GROUP_OFFICE = "/Office"
    }

    // Mine
    object Mine {
        const val GROUP_MINE = "/Mine"
    }

}
