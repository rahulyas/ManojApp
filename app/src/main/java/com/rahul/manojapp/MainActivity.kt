package com.rahul.manojapp

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.rahul.manojapp.databinding.ActivityMainBinding
import java.text.DateFormat
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var uri: Uri? = null
    var imageUrl: String? = null
    private val SELECT_IMAGE_REQUEST = 1 // You can choose any request code you like.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        Util().requestStoragePermission(this)
        binding.sproduct.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val product = adapterView.getItemAtPosition(position).toString()
                if(product == "Product_1"){
                    binding.uploaddetails.visibility = View.VISIBLE
                    Toast.makeText(this@MainActivity,"Product_1 Selected",Toast.LENGTH_SHORT).show()
                }else if(product == "Product_2"){
                    binding.uploaddetails.visibility = View.GONE
                    Toast.makeText(this@MainActivity,"Product_2 Selected",Toast.LENGTH_SHORT).show()
                }else if(product == "Product_3"){
                    binding.uploaddetails.visibility = View.GONE
                    Toast.makeText(this@MainActivity,"Product_3 Selected",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
            }
        }

        binding.uploadImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, SELECT_IMAGE_REQUEST)
        }

        binding.savebutton.setOnClickListener {
            if(isValidation()){
                saveData()
            }else{

            }
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                uri = data.data
                binding.uploadImage.setImageURI(uri)
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Something went wrong no image upload selected",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun isValidation(): Boolean {
        return if (binding.productNameEdit.text.toString().trim().isEmpty()) {
            Toast.makeText(
                this,
                getString(R.string.product_name),
                Toast.LENGTH_SHORT
            ).show()
            false
        } else if (binding.productRateEdit.text.toString().trim().isEmpty()) {
            Toast.makeText(
                this,
                getString(R.string.product_rate),
                Toast.LENGTH_SHORT
            ).show()
            false
        } else if (binding.productDiscountrateEdit.text.toString().trim().isEmpty()) {
            Toast.makeText(
                this,
                getString(R.string.discount_rate),
                Toast.LENGTH_SHORT
            ).show()
            false
        } else if (binding.productDescriptionEdit.text.toString().trim().isEmpty()) {
            Toast.makeText(
                this,
                getString(R.string.Description),
                Toast.LENGTH_SHORT
            ).show()
            false
        }
        else {
            true
        }

    }

    fun saveData() {
        val storageReference = FirebaseStorage.getInstance().reference.child("Android Images").child(
                uri!!.lastPathSegment!!
            )
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        val dialog = builder.create()
        dialog.show()
        storageReference.putFile(uri!!).addOnSuccessListener { taskSnapshot ->
            val uriTask = taskSnapshot.storage.downloadUrl
            while (!uriTask.isComplete);
            val uriImage = uriTask.result
            imageUrl = uriImage.toString()
            uploadData()
            dialog.dismiss()
        }.addOnFailureListener { e ->
            dialog.dismiss()
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun uploadData() {
        val productName: String = binding.productNameEdit.text.toString().trim()
        val productRate: String = binding.productRateEdit.text.toString().trim()
        val productDiscountrate: String = binding.productDiscountrateEdit.text.toString().trim()
        val productDescription: String = binding.productDescriptionEdit.text.toString().trim()
        val dataClass = DataClass(productName, productRate, productDiscountrate,productDescription, imageUrl)

        // here we changing the child from title to currentDate
        // because we will be updating title as well and it may affect child value.
        val currentdate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
        //Firebase Tutorials is the table name
        FirebaseDatabase.getInstance().getReference("Product Table").child(currentdate)
            .setValue(dataClass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Data Saved", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


}