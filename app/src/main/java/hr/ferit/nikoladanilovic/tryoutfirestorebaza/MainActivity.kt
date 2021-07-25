package hr.ferit.nikoladanilovic.tryoutfirestorebaza

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.*
import hr.ferit.nikoladanilovic.tryoutfirestorebaza.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private val KEY_TITLE = "title"
    private val KEY_DESCRIPTION = "description"

    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private val noteBookRef : CollectionReference = db.collection("Notebook")
    private lateinit var noteListener : ListenerRegistration

    private lateinit var  mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        mainBinding.buttonSave.setOnClickListener { addData() }
        mainBinding.buttonLoad.setOnClickListener { loadData() }
        setContentView(mainBinding.root)
    }



    //U onStart metodi se treba implementirati SNAPSHOT LISTENER
    override fun onStart() {
        super.onStart()


        noteListener = noteBookRef.addSnapshotListener { value, e ->    //Ako se zeli primjeniti query , onda se pise: noteBookRef.whereEqualTo("title", "nekinaslov").addSnapshotListener { ..sve isto.. }
            var data = ""

            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            /*
            val cities = ArrayList<String>()
            for (doc in value!!) {
                doc.getString("name")?.let {
                    cities.add(it)
                }
            }

             */

            for (doc in value!!) {
                val note = doc.toObject(Note::class.java)
                note.setDocumentId(doc.id)


                data += "ID: " + note.getDocumentId() + "\nTitle: " + note.getTitle() + "\n" + "Description: " + note.getDescription() + "\n\n"
            }

            mainBinding.tvNoteContent.text = data


        }
    }

    //SNAPSHOT LISTENER SE treba OTKACITI kada se ne koristi aplikacija u onStop metodi
    override fun onStop() {
        super.onStop()
        noteListener.remove()
    }

    private fun loadData() {
        var data = ""

        noteBookRef.get()   //ovime se dohvacaju svi dokumenti iz kolekcije, ako se hoce radit QUERY, onda se pise noteBookRef.whereEqualTo("capital", true).get().addOnSuccess...
            .addOnSuccessListener { result ->
                for (document in result) {
                    //tu bi se trebalo dodavati pojedine dokumente u polje elemenata tog tipa, u ovom slucaju tipa Note
                    Log.d(TAG, "${document.id} => ${document.data}")

                    val note = document.toObject(Note::class.java)
                    note.setDocumentId(document.id)

                    //note.setDocumentId(document.id) //treba se napraviti setDocumentId metoda u data klasi Note i treba se stvoriti taj setter (a i getter treba ako su clanovi private)
                    //kod getera (getDocumentId()) treba staviti @Exclude

                    data += "ID: " + note.getDocumentId() + "\nTitle: " + note.getTitle() + "\n" + "Description: " + note.getDescription() + "\n\n"
                }
                mainBinding.tvNoteContent.text = data

                //noteBookRef.document(documentId) // PRISTUP DOKUMENTU PREKO NJEGOVOG ID-A
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
                mainBinding.tvNoteContent.text = ""
            }
    }

    private fun addData() {
        val title = mainBinding.editTextTitle.text.toString()
        val description = mainBinding.editTextDescription.text.toString()

        // koristenje CUSTOM OBJECTA
        val note = Note("", title, description)

        noteBookRef.add(note)
    }






}