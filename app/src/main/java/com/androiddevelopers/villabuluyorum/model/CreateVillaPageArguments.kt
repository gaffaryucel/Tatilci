package com.androiddevelopers.villabuluyorum.model

import android.net.Uri
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import java.io.Serializable

data class CreateVillaPageArguments(
    var coverImage: Uri? = null,
    var otherImages: List<Uri>,
    var villa: Villa
) : Serializable
