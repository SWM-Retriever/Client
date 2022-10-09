package org.retriever.dailypet.util

import android.net.Uri

import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks

object DynamicLinksUtil {

    fun createDynamicLink(): Uri {
        val baseUrl = Uri.parse("https://dailypet.page.link/invite")
        val domain = "https://dailypet.page.link/invite"

        val link = FirebaseDynamicLinks.getInstance()
            .createDynamicLink()
            .setLink(baseUrl)
            .setDomainUriPrefix(domain)
            .setAndroidParameters(DynamicLink.AndroidParameters.Builder("org.retriever.dailypet").build())
            .buildDynamicLink()

        return link.uri
    }
}