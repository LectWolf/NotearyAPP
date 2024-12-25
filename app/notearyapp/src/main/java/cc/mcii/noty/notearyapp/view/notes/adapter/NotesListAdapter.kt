package cc.mcii.noty.notearyapp.view.notes.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cc.mcii.noty.core.model.Note
import cc.mcii.noty.notearyapp.R
import cc.mcii.noty.notearyapp.databinding.ItemNoteBinding
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class NotesListAdapter(
    private val onNoteClick: (Note) -> Unit
) : ListAdapter<Note, NotesListAdapter.NoteViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NoteViewHolder(
        ItemNoteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position), onNoteClick)
    }

    inner class NoteViewHolder(
        private val binding: ItemNoteBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private var x1 = 0f
        private var y1 = 0f

        @SuppressLint("ClickableViewAccessibility")
        fun bind(note: Note, onNoteClick: (Note) -> Unit) {
            with(binding) {
                textTitle.text = note.title
                textNote.text = note.note
                textTime.text = formatNoteTime(root.context, note.update)
                pinningIcon.isVisible = note.isPinned
                deleteIcon.visibility = View.GONE
                root.setOnClickListener { onNoteClick(note) }
                //点击动画
//                root.setOnTouchListener { view, event ->
//                    onTouchEvent(view, event, note)
//                }
            }
        }

        private fun onTouchEvent(view: View, event: MotionEvent, note: Note): Boolean {
            var (x2, y2) = listOf(0f, 0f)
            Log.d("调试", "${event.action.toString()}")
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    x1 = event.x
                    y1 = event.y
                    // 按下时缩放
                    view.animate()
                        .scaleX(0.90f)
                        .scaleY(0.90f)
                        .setDuration(100)
                        .start()
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // 松开或取消时恢复原状
                    view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .withEndAction {
                            if (event.action == MotionEvent.ACTION_UP) {
                                x2 = event.x
                                y2 = event.y
                                Log.d("调试", "x1:$x1,x2:$x2,y1:$y1,y2:$y2")
                                onNoteClick(note)
                            }
                        }
                        .start()
                }
            }
            return true
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldItem: Note, newItem: Note) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Note, newItem: Note) = oldItem == newItem
        }
    }

    private fun formatNoteTime(context: Context, timestamp: Long): String {
        val instant = Instant.ofEpochMilli(timestamp)
        val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
        val now = LocalDateTime.now()

        return if (localDateTime.toLocalDate() == now.toLocalDate()) {
            DateTimeFormatter.ofPattern(context.getString(R.string.fmt_current))
                .format(localDateTime)
        } else {
            DateTimeFormatter.ofPattern(context.getString(R.string.fmt_date)).format(localDateTime)
        }
    }
}
