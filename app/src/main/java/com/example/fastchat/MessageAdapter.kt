package com.example.fastchat

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fastchat.databinding.DeleteLayoutBinding
import com.example.fastchat.databinding.DeleteReceiverBinding
import com.example.fastchat.databinding.ReceiveMsgBinding
import com.example.fastchat.databinding.SendMsgBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter(
    private val context: Context,
    private val messages: ArrayList<Message>,
    private val senderRoom: String,
    private val receiverRoom: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val ITEM_SENT = 1
        private const val ITEM_RECEIVE = 2
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.senderId == FirebaseAuth.getInstance().uid) {
            ITEM_SENT
        } else {
            ITEM_RECEIVE
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_SENT) {
            val view = LayoutInflater.from(context).inflate(R.layout.send_msg, parent, false)
            SentMsgHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.receive_msg, parent, false)
            ReceiveMsgHolder(view)
        }
    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        if (holder is SentMsgHolder) {

            val timestamp = message.timeStamp

            val timeString = timestamp?.let { convertTimestampToTime(it) } // Output: "12:22 AM"
            holder.binding.time.text = timeString

            if (message.message == "photo") {
                holder.binding.image.visibility = View.VISIBLE
                holder.binding.sendMessage.visibility = View.GONE
                holder.binding.hLinear.setBackgroundColor(Color.TRANSPARENT)
                holder.binding.imageCard.visibility = View.VISIBLE
                Glide.with(context)
                    .load(message.imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.binding.image)

                // Set click listener for the image
                holder.binding.image.setOnClickListener {
                    val intent = Intent(context, ShowImage::class.java)
                    intent.putExtra("imageUrl", message.imageUrl)
                    context.startActivity(intent)
                }
                holder.binding.image.setOnLongClickListener {
                    showDeleteDialog(message)
                    true
                }
            } else {
                holder.binding.sendMessage.text = message.message
                holder.binding.image.visibility = View.GONE
                holder.binding.imageCard.visibility = View.GONE
            }

            holder.itemView.setOnLongClickListener {
                showDeleteDialog(message)
                true
            }
        } else if (holder is ReceiveMsgHolder) {

            val timestamp = message.timeStamp

            val timeString = timestamp?.let { convertTimestampToTime(it) } // Output: "12:22 AM"
            holder.binding.time.text = timeString

            if (message.message == "photo") {
                holder.image.visibility = View.VISIBLE
                holder.sendMessage.visibility = View.GONE
                holder.binding.imageCard.visibility = View.VISIBLE
                holder.binding.hLinear.setBackgroundColor(Color.TRANSPARENT)

                Glide.with(context)
                    .load(message.imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.image)

                holder.binding.image.setOnClickListener {
                    val intent = Intent(context, ShowImage::class.java)
                    intent.putExtra("imageUrl", message.imageUrl)
                    context.startActivity(intent)
                }
                holder.binding.image.setOnLongClickListener {
                    showDeleteDialog(message)
                    true
                }

            } else {
                holder.sendMessage.text = message.message
                holder.image.visibility = View.GONE
                holder.binding.imageCard.visibility = View.GONE
            }

            holder.itemView.setOnLongClickListener {
                showDeleteDialogReceiver(message)
                true
            }
        }
    }


    private fun showDeleteDialogReceiver(message: Message) {
        val view = LayoutInflater.from(context).inflate(R.layout.delete_receiver, null)
        val binding: DeleteReceiverBinding = DeleteReceiverBinding.bind(view)
        val view2 = LayoutInflater.from(context).inflate(R.layout.receive_msg, null)
        val binding2: ReceiveMsgBinding = ReceiveMsgBinding.bind(view2)

        val dialog = AlertDialog.Builder(context)
            .setTitle("Delete Message")
            .setView(binding.root)
            .create()

        binding.delete.setOnClickListener {
            message.messageId?.let { messageId ->
                FirebaseDatabase.getInstance().reference.child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .child(messageId)
                    .setValue(null)
            }

            dialog.dismiss()
        }

        binding.cancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun showDeleteDialog(message: Message) {
        val view = LayoutInflater.from(context).inflate(R.layout.delete_layout, null)
        val binding: DeleteLayoutBinding = DeleteLayoutBinding.bind(view)

        val dialog = AlertDialog.Builder(context)
            .setTitle("Delete Message")
            .setView(binding.root)
            .create()

        binding.everyone.setOnClickListener {
            message.message = "This message was deleted!"
            message.messageId?.let { messageId ->
                val messageRef = FirebaseDatabase.getInstance().reference.child("chats")

                messageRef.child(senderRoom).child("messages").child(messageId)
                    .setValue(message)

                messageRef.child(receiverRoom).child("messages").child(messageId)
                    .setValue(message)
            }

            dialog.dismiss()
        }

        binding.delete.setOnClickListener {
            message.messageId?.let { messageId ->
                FirebaseDatabase.getInstance().reference.child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .child(messageId)
                    .setValue(null)
            }

            dialog.dismiss()
        }

        binding.cancel.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }
    inner class SentMsgHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: SendMsgBinding = SendMsgBinding.bind(itemView)
        val sendMessage: TextView = binding.sendMessage
        val image: ImageView = binding.image
    }

    inner class ReceiveMsgHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: ReceiveMsgBinding = ReceiveMsgBinding.bind(itemView)
        val sendMessage: TextView = binding.sendMessage
        val image: ImageView = binding.image
    }

    fun convertTimestampToTime(timestamp: Long): String {
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        return sdf.format(date)
    }

}
