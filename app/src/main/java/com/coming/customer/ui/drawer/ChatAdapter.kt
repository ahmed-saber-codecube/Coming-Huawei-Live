package com.coming.customer.ui.drawer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.R
import com.coming.customer.data.pojo.chat.ChatMessage
import com.coming.customer.util.DateTimeUtility
import com.coming.customer.util.loadUrlRoundedCorner
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.row_message_received.view.*
import kotlinx.android.synthetic.main.row_message_sent.view.*
import java.util.*


/**
 * Created by hlink4 on 10/3/18.
 */


/*class ChatAdapter(private val modelList: ArrayList<ChatMessage>, private var onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val SENDER = 0
    private val RECEIVER = 1
    private lateinit var myId: String


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        if (viewType == SENDER) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.row_message_sent, parent, false)
            return DataHolder(view)
        } else {
            view = LayoutInflater.from(parent.context).inflate(R.layout.row_message_received, parent, false)
            return DataHolder1(view)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chatMessage = modelList.get(position)

        if (holder is DataHolder) {
            holder.textViewMessageSent.setText(chatMessage.message)

            val date = DateTimeUtility.parseDate(chatMessage.time, "dd-MM-yyyy HH:mm:ss")
            holder.textViewTimeStampSent.setText(
                Objects.requireNonNull(DateTimeUtility.formatDate(date, "hh:mm a"))?.replace("AM", "am")?.replace("PM", "pm")
            )

            if (position == 0) {
                val date = DateTimeUtility.parseDate(chatMessage.time, "dd-MM-yyyy HH:mm:ss")
                holder.textViewTimeStampSent.text = DateTimeUtility.formatDate(date, "dd MMM, yyyy")
                holder.textViewTimeStampSent.visibility = View.VISIBLE
            } else {
                val dateTime = DateTime.parse(
                    chatMessage.time,
                    DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss")
                )
                val dateTime1 = DateTime.parse(
                    modelList.get(position - 1).time,
                    DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss")
                )
                if (dateTime.withTimeAtStartOfDay() == dateTime1.withTimeAtStartOfDay()) {
                    holder.textViewTimeStampSent.visibility = View.GONE

                } else {
                    val date = DateTimeUtility.parseDate(
                        chatMessage.time,
                        "dd-MM-yyyy HH:mm:ss"
                    )
                    holder.textViewTimeStampSent.text = DateTimeUtility.formatDate(date, "dd MMM, yyyy")
                    holder.textViewTimeStampSent.visibility = View.VISIBLE
                }
            }
        } else if (holder is DataHolder1) {
            holder.textViewMessageReceived.setText(chatMessage.message)

            val date = DateTimeUtility.parseDate(chatMessage.time, "dd-MM-yyyy HH:mm:ss")
            holder.textViewTimeStampReceived.setText(
                Objects.requireNonNull(DateTimeUtility.formatDate(date, "hh:mm a"))?.replace("AM", "am")?.replace("PM", "pm")
            )

            if (position == 0) {
                val date =
                    DateTimeUtility.parseDate(chatMessage.time, "dd-MM-yyyy HH:mm:ss")
                holder.textViewTimeStampReceived.text = DateTimeUtility.formatDate(date, "dd MMM, yyyy")
                holder.textViewTimeStampReceived.visibility = View.VISIBLE
            } else {
                val dateTime = DateTime.parse(
                    chatMessage.time,
                    DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss")
                )
                val dateTime1 = DateTime.parse(
                    modelList.get(position - 1).time,
                    DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss")
                )
                if (dateTime.withTimeAtStartOfDay() == dateTime1.withTimeAtStartOfDay()) {
                    holder.textViewTimeStampReceived.visibility = View.GONE

                } else {
                    val date = DateTimeUtility.parseDate(
                        chatMessage.time,
                        "dd-MM-yyyy HH:mm:ss"
                    )
                    holder.textViewTimeStampReceived.text = DateTimeUtility.formatDate(date, "dd MMM, yyyy")
                    holder.textViewTimeStampReceived.visibility = View.VISIBLE
                }
            }
        }

    }


    override fun getItemCount(): Int {
        return modelList.size
    }

    *//*override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) {
            SENDER
        } else {
            RECEIVER
        }

    }*//*


    override fun getItemViewType(position: Int): Int {
        return modelList.get(position).type
    }


    interface OnItemClickListener {

        fun onItemClick(pos: Int)

    }


    inner class DataHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        init {

        }


    }

    inner class DataHolder1(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        init {

        }


    }

}*/



class ChatAdapter(private val modelList: ArrayList<ChatMessage>, private var onChatClickListener: OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val SENDER = 0
    private val RECEIVER = 1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        if (viewType == SENDER) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.row_message_sent, parent, false)
            return DataHolder(view)
        } else {
            view = LayoutInflater.from(parent.context).inflate(R.layout.row_message_received, parent, false)
            return DataHolder1(view)
        }
        /*return if (viewType == SENDER) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.row_message_sent, parent, false)
            DataHolder(view)
        } else {
            view = LayoutInflater.from(parent.context).inflate(R.layout.row_message_received, parent, false)
            DataHolder1(view)
        }*/
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        if (holder is DataHolder) {
            try {
                val date = DateTimeUtility.parseDate(modelList.get(position).time, "dd-MM-yyyy HH:mm:ss")
                holder.containerView.textViewTimeStampSent.setText(Objects.requireNonNull(DateTimeUtility.formatDate(date, "hh:mm a"))?.replace("AM", "am")?.replace("PM", "pm"))
            } catch (e: Exception) {

            }


            /*holder.textViewTimeStampSent.text = TimeConvertUtils.dateTimeConvertLocalToLocalDisplay(
                modelList.get(position).time,
                "MMM d, yyyy hh:mm a",
                "MMM d, yyyy hh:mm a"
            )*/

            if (modelList.get(position).messageType != null && modelList.get(position).messageType.equals("T") && modelList.get(position).message != null) {
                holder.containerView.textViewMessageSent.visibility = View.VISIBLE
                holder.containerView.imageViewMessageSent.visibility = View.GONE
                holder.containerView.textViewMessageSent.text = modelList.get(position).message

            } else {
                if (modelList.get(position).messageType != null && modelList.get(position).messageType.equals("M") && modelList.get(position).chatImage != null) {
                    holder.containerView.textViewMessageSent.visibility = View.GONE
                    holder.containerView.imageViewMessageSent.visibility = View.VISIBLE
                    holder.containerView.imageViewChatMessage.loadUrlRoundedCorner(modelList.get(position).chatImage, 0, 10)
                }

            }

        } else {
            val date = DateTimeUtility.parseDate(modelList.get(position).time, "dd-MM-yyyy HH:mm:ss")
            (holder as DataHolder1).containerView.textViewTimeStampReceived.setText(Objects.requireNonNull(DateTimeUtility.formatDate(date, "hh:mm a"))?.replace("AM", "am")?.replace("PM", "pm"))

            /*(holder as DataHolder1).textViewTimeStampReceived.text = TimeConvertUtils.dateTimeConvertLocalToLocalDisplay(
                modelList.get(position).time,
                "MMM d, yyyy hh:mm a",
                "MMM d, yyyy hh:mm a"
            )*/

            if (modelList.get(position).messageType != null && modelList.get(position).messageType.equals("T") && modelList.get(position).message != null) {
                holder.containerView.textViewMessageReceived.visibility = View.VISIBLE
                holder.containerView.imageViewMessageReceived.visibility = View.GONE
                holder.containerView.textViewMessageReceived.text = modelList.get(position).message
            } else {
                if (modelList.get(position).messageType != null && modelList.get(position).messageType.equals("M") && modelList.get(position).chatImage != null) {
                    holder.containerView.textViewMessageReceived.visibility = View.GONE
                    holder.containerView.imageViewMessageReceived.visibility = View.VISIBLE
                    holder.containerView.imageViewChatMessageReceived.loadUrlRoundedCorner(modelList.get(position).chatImage, 0, 10)
                }
            }


        }


    }


    override fun getItemCount(): Int {
        return modelList.size
    }

    override fun getItemViewType(position: Int): Int {
        return modelList.get(position).type

        /*return if (modelList.get(position).isUserType) {
            SENDER
        } else {
            RECEIVER
        }*/
    }

    interface OnItemClickListener {
        fun onItemClick(pos: Int)
    }


    inner class DataHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        init {

        }
    }

    inner class DataHolder1(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        init {

        }
    }

}


/*protoType chat code */

/*
class ChatAdapter(private var dataSet: ArrayList<ChatData>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    companion object {
        const val TIMESTAMP = 1
        const val SENT_MESSAGE = 2
        const val RECEIVED_MESSAGE = 3
    }

    val lastMessage: Int
        get() = dataSet.size

    override fun getItemCount(): Int = dataSet.size

    override fun getItemViewType(position: Int): Int {
        return when {
            dataSet[position] is ReceivedMessage -> RECEIVED_MESSAGE
            dataSet[position] is SentMessage -> SENT_MESSAGE
            else -> TIMESTAMP
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType){
            SENT_MESSAGE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.row_message_sent, parent, false)
                SentMessageViewHolder(view)
            }
            RECEIVED_MESSAGE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.row_message_received, parent, false)
                ReceivedMessageViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.row_message_timestamp, parent, false)
                TimeStampViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SentMessageViewHolder -> {
                holder.bind(dataSet[position])
            }
            is ReceivedMessageViewHolder -> {
                holder.bind(dataSet[position])
            }
            is TimeStampViewHolder -> {
                holder.bind(dataSet[position])
            }
        }
    }

    fun addSentMessage(){
//        dataSet.add(message)
        notifyItemInserted(dataSet.size)
    }



    inner class SentMessageViewHolder(view: View): RecyclerView.ViewHolder(view){
        fun bind(chatData: ChatData) = with(itemView){
            textViewMessageSent.text = chatData.message
            textViewTimeStampSent.text = chatData.timeStamp
        }
    }

    inner class ReceivedMessageViewHolder(view: View): RecyclerView.ViewHolder(view){
        fun bind(chatData: ChatData) = with(itemView){
            textViewMessageReceived.text = chatData.message
            textViewTimeStampReceived.text = chatData.timeStamp
        }
    }

    inner class TimeStampViewHolder(view: View): RecyclerView.ViewHolder(view){
        fun bind(message: ChatData) = with(itemView){
            textViewTimeStamp.text = message.timeStamp
        }
    }
}*/
