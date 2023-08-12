package com.example.fastchat

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fastchat.databinding.DeleteLayoutBinding
import com.example.fastchat.databinding.DeleteReceiverBinding
import com.example.fastchat.databinding.ReceiveMsgBinding
import com.example.fastchat.databinding.SendMsgBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    @OptIn(DelicateCoroutinesApi::class)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        if (holder is SentMsgHolder) {
            val timestamp = message.timeStamp
            val timeString = timestamp?.let { convertTimestampToTime(it) }
            holder.binding.time.text = timeString

            when (message.message) {
                "photo" -> {
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
                }
                "voice" -> {
                    holder.binding.sendMessage.visibility = View.GONE
                    holder.binding.audioLayout.visibility = View.VISIBLE
                    holder.binding.imageCard.visibility = View.GONE

                    val audioFileUrl = message.imageUrl
                    val mediaPlayer = MediaPlayer()
                    mediaPlayer.setDataSource(audioFileUrl)
                    mediaPlayer.prepare()

                    // Get the duration of the audio file and update the TextView
                    val durationInMillis = mediaPlayer.duration
                    val durationString = convertDurationToString(durationInMillis)
                    holder.binding.audioLength.text = durationString

                    holder.binding.play.setOnClickListener {
                        // Use Kotlin Coroutine to play audio on a background thread
                        GlobalScope.launch(Dispatchers.IO) {
                            val mediaPlayer = MediaPlayer()

                            try {
                                mediaPlayer.setDataSource(audioFileUrl)
                                mediaPlayer.prepare()

                                withContext(Dispatchers.Main) {
                                    // Get the duration of the audio file and update the TextView on the main thread
                                    val durationInMillis = mediaPlayer.duration
                                    val durationString = convertDurationToString(durationInMillis)
                                    holder.binding.audioLength.text = durationString
                                }

                                mediaPlayer.start()
                                holder.binding.play.setImageResource(R.drawable.pause)

                                mediaPlayer.setOnCompletionListener {
                                    holder.binding.play.setImageResource(R.drawable.play)
                                    mediaPlayer.reset()
                                    mediaPlayer.release()
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        "Unable to play voice note",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                mediaPlayer.release()
                            }
                        }
                    }
                }
                "This message was deleted!" -> {
                    holder.binding.hLinear.setBackgroundResource(R.drawable.deleted_send)
                    holder.binding.sendMessage.text = "This message was deleted!"
                }
                else -> {
                    holder.binding.sendMessage.text = message.message
                    holder.binding.image.visibility = View.GONE
                    holder.binding.imageCard.visibility = View.GONE
                }
            }

            holder.itemView.setOnLongClickListener {
                showDeleteDialog(message)
                true
            }
        } else if (holder is ReceiveMsgHolder) {
            val timestamp = message.timeStamp
            val timeString = timestamp?.let { convertTimestampToTime(it) }
            holder.binding.time.text = timeString

            when (message.message) {
                "photo" -> {
                    holder.binding.image.visibility = View.VISIBLE
                    holder.binding.sendMessage.visibility = View.GONE
                    holder.binding.imageCard.visibility = View.VISIBLE
                    holder.binding.hLinear.setBackgroundColor(Color.TRANSPARENT)

                    Glide.with(context)
                        .load(message.imageUrl)
                        .placeholder(R.drawable.placeholder)
                        .into(holder.binding.image)

                    holder.binding.image.setOnClickListener {
                        val intent = Intent(context, ShowImage::class.java)
                        intent.putExtra("imageUrl", message.imageUrl)
                        context.startActivity(intent)
                    }
                    holder.binding.image.setOnLongClickListener {
                        showDeleteDialogReceiver(message)
                        true
                    }
                }
                "voice" -> {
                    holder.binding.sendMessage.visibility = View.GONE
                    holder.binding.audioLayout.visibility = View.VISIBLE
                    holder.binding.imageCard.visibility = View.GONE

                    val audioFileUrl = message.imageUrl
                    val mediaPlayer = MediaPlayer()
                    mediaPlayer.setDataSource(audioFileUrl)
                    mediaPlayer.prepare()

                    // Get the duration of the audio file and update the TextView
                    val durationInMillis = mediaPlayer.duration
                    val durationString = convertDurationToString(durationInMillis)
                    holder.binding.audioLength.text = durationString

                    holder.binding.play.setOnClickListener {
                        // Use Kotlin Coroutine to play audio on a background thread
                        GlobalScope.launch(Dispatchers.IO) {
                            val mediaPlayer = MediaPlayer()

                            try {
                                mediaPlayer.setDataSource(audioFileUrl)
                                mediaPlayer.prepare()

                                withContext(Dispatchers.Main) {
                                    // Get the duration of the audio file and update the TextView on the main thread
                                    val durationInMillis = mediaPlayer.duration
                                    val durationString = convertDurationToString(durationInMillis)
                                    holder.binding.audioLength.text = durationString
                                }

                                mediaPlayer.start()
                                holder.binding.play.setImageResource(R.drawable.pause)

                                mediaPlayer.setOnCompletionListener {
                                    holder.binding.play.setImageResource(R.drawable.play)
                                    mediaPlayer.release()
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        "Unable to play voice note",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                mediaPlayer.release()
                            }
                        }
                    }
                }
                "This message was deleted!" -> {
                    holder.binding.hLinear.setBackgroundResource(R.drawable.receive_drawable)
                    holder.binding.sendMessage.text = "This message was deleted"
                }else -> {
                    holder.binding.sendMessage.text = message.message
                    holder.binding.image.visibility = View.GONE
                    holder.binding.imageCard.visibility = View.GONE
                }
            }

            holder.itemView.setOnLongClickListener {
                showDeleteDialogReceiver(message)
                true
            }
        }
    }

    private fun releaseMediaPlayer(mediaPlayer: MediaPlayer) {
        mediaPlayer.reset()
        mediaPlayer.release()
    }

    private fun showDeleteDialogReceiver(message: Message) {
        val view = LayoutInflater.from(context).inflate(R.layout.delete_receiver, null)
        val binding: DeleteReceiverBinding = DeleteReceiverBinding.bind(view)
        val view2 = LayoutInflater.from(context).inflate(R.layout.receive_msg, null)

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
    }

    inner class ReceiveMsgHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: ReceiveMsgBinding = ReceiveMsgBinding.bind(itemView)
    }

    private fun convertTimestampToTime(timestamp: Long): String {
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        return sdf.format(date)
    }

    private fun convertDurationToString(durationInMillis: Int): String {
        val hours = (durationInMillis / (1000 * 60 * 60)) % 24
        val minutes = (durationInMillis / (1000 * 60)) % 60
        val seconds = (durationInMillis / 1000) % 60

        return when {
            hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
            minutes > 0 -> String.format("%02d:%02d", minutes, seconds)
            else -> String.format("00:%02d", seconds)
        }
    }
}
