import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.example.applepie.R
import com.example.applepie.model.Music

class MusicListAdapter(
    private val musicList: List<Music>,
    private val initialSelectedMusic: Music) :
    RecyclerView.Adapter<MusicListAdapter.ViewHolder>() {

    private var selectedItemPosition: Int = RecyclerView.NO_POSITION
    init {
        selectedItemPosition = musicList.indexOf(initialSelectedMusic)
    }

    private var onItemClickListener: ((Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val radioButton: RadioButton = itemView.findViewById(R.id.radio_music)

        init {
            // Set click listener on the RadioButton
            radioButton.setOnClickListener {
                val adapterPosition = adapterPosition
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    // Update selected item position
                    selectedItemPosition = adapterPosition
                    // Notify adapter about the item change
                    notifyDataSetChanged()
                    // Invoke click listener
                    onItemClickListener?.invoke(adapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_music_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = musicList[position]
        holder.radioButton.text = currentItem.name
        // Set checked status based on the position
        holder.radioButton.isChecked = position == selectedItemPosition
    }

    override fun getItemCount() = musicList.size
}
