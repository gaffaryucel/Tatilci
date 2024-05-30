package com.androiddevelopers.villabuluyorum.model

import android.net.Uri
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import java.io.Serializable

data class VillaPageArgumentsModel(
    var villaId: String? = null,
    var coverImage: Uri? = null,
    var otherImages: List<Uri> = listOf(),
    var villa: Villa,
) : Serializable
