package com.thehoick.photolandia.models

import java.util.*

class Album(val id: Int, val name: String, val description: String, val createdAt: Date, val updatedAt: Date, val photos: Array<Photo>)