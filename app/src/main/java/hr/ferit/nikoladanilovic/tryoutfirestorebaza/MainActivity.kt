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
    private val noteRef : DocumentReference = db.collection("Notebook").document("My first Note")    //Moze se napisati i db.document("Notebook/My First Note") itd..
    private lateinit var noteListener : ListenerRegistration

    private lateinit var  mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        mainBinding.buttonSave.setOnClickListener { saveData() }
        mainBinding.buttonLoad.setOnClickListener { loadData() }
        mainBinding.buttonUpdate.setOnClickListener { updateDescription() }
        mainBinding.buttonDeleteDesc.setOnClickListener { deleteDescription() }
        mainBinding.buttonDeleteNote.setOnClickListener { deleteNote() }
        setContentView(mainBinding.root)
    }



    //U onStart metodi se treba implementirati SNAPSHOT LISTENER
    override fun onStart() {
        super.onStart()
        noteListener = noteRef.addSnapshotListener { snapshot, e ->
            if(e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if(snapshot != null && snapshot.exists()){
                //Log.d(TAG, "Current data: ${snapshot.data}")
                //mainBinding.tvNoteContent.text = "Title: " + snapshot.getString(KEY_TITLE) + "\n" + "Description: " + snapshot.getString(KEY_DESCRIPTION)

                //Koristenjem CUSTOM OBJECTA
                val note = snapshot.toObject(
                    Note::class.java
                )

                mainBinding.tvNoteContent.text = "Title: " + note?.title + "\n" + "Description: " + note?.description

            } else {
                Log.d(TAG, "Current data: null")
                mainBinding.tvNoteContent.text = ""
            }
        }
    }

    //SNAPSHOT LISTENER SE treba OTKACITI kada se ne koristi aplikacija u onStop metodi
    override fun onStop() {
        super.onStop()
        noteListener.remove()
    }

    private fun loadData() {
        noteRef.get()
            .addOnSuccessListener { document ->
                if(document != null){
                    //Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    // u javi se moze jos i ovako napraviti.. Map<String, Object> note = documentSnapshot.getData();
                    //mainBinding.tvNoteContent.text = document.getString(KEY_TITLE)  //dohvacanje po klucu, odnosno nazivu svojstva

                    //Koristenjem CUSTOM DATA OBJECT-a
                    val note = document.toObject(
                        Note::class.java
                    )

                    mainBinding.tvNoteContent.text = "Title: " + note?.title + "\n" + "Description: " + note?.description

                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    private fun saveData() {
        val title = mainBinding.editTextTitle.text.toString()
        val description = mainBinding.editTextDescription.text.toString()

        //val note: MutableMap<String, Any> = hashMapOf()    //ovdje se koristi HASH MAP
        //note.put(KEY_TITLE, title)
        //note.put(KEY_DESCRIPTION, description)
        // mogu se proizvoljno dodavat imena atributa: note.put("desc2", description)

        // koristenje CUSTOM OBJECTA
        val note = Note(title, description)


        //db.collection().document().collection().document().set()  .... tako cu trebat
        //mogu mijenjati ime dokumenta user1, user2, ili pomocu jedinstvenih username-a, tako sto mijenjam i formatiram stringove
        noteRef.set(note)
    }


    //Mijenja SAMO description atribut (MERGE i UPDATE)
    private fun updateDescription() {
        val description = mainBinding.editTextDescription.text.toString()

        val note: MutableMap<String, Any> = hashMapOf()
        note.put(KEY_DESCRIPTION, description)

        //MERGE - ako vec postoji dokument, nadogradi ga, ako ne postoji stvori ga sa prosljedjenom mapom
        noteRef.set(note, SetOptions.merge())

        //moze se koristiti i UPDATE naredba - za razliku od MERGE-a , UPDATE ako vec ne postoji dokument nece ga moci stvoriti
        //noteRef.update(note)
        //ili
        //noteRef.update(KEY_DESCRIPTION, description)
    }

    //Brisanje atributa description s naredbom DELETE
    private fun deleteDescription() {
        //1. nacin (korisnije ako se vise atributa brise, jer ih se vise moze staviti u mapu)
        /*
        val note: MutableMap<String, Any> = hashMapOf()
        note.put(KEY_DESCRIPTION, FieldValue.delete())
        noteRef.update(note)
        */

        //2. nacin - brze i korisno kada se samo jedan atribut brise
        noteRef.update(KEY_DESCRIPTION, FieldValue.delete())

        //na oba nacina se moze dodati onSuccessListener i onFailureListener, ako je potrebno
    }


    private fun deleteNote() {
        noteRef.delete()
    }



}