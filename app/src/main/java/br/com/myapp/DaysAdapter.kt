    package br.com.myapp
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Button
    import androidx.recyclerview.widget.RecyclerView

    class DaysAdapter(private val days: List<String>, private val onClick: (String) -> Unit) :
        RecyclerView.Adapter<DaysAdapter.DayViewHolder>() {

        class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val dayButton: Button = view.findViewById(R.id.dayButton)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.day_item, parent, false)
            return DayViewHolder(view)
        }

        override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
            holder.dayButton.text = days[position]
            holder.dayButton.setOnClickListener { onClick(days[position]) }
        }

        override fun getItemCount() = days.size
    }
