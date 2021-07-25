package hr.ferit.nikoladanilovic.tryoutfirestorebaza

import com.google.firebase.firestore.Exclude

class Note(
    private var documentId : String = "",
    private var title: String = "",
    private var description: String = ""
){
    @Exclude
    fun getDocumentId() : String {
        return documentId
    }

    fun setDocumentId(documentId: String){
        this.documentId = documentId
    }

    /*
    constructor(title: String, description: String) : {
        this.title = title
        this.description = description
    }

     */

    fun getTitle() : String {
        return title
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun getDescription() : String {
        return description
    }

    fun setDescription(description: String) {
        this.description = description
    }


}
