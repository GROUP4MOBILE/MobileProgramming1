@file:Suppress("KotlinConstantConditions")

package com.example.tasks

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.tasks.adapter.TodoAdapter
class RecyclerItemTouchHelper(private val adapter: TodoAdapter) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }
    @SuppressLint("SuspiciousIndentation")
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.getAdapterPosition()

            // Handle swiping actions for main tasks
            if (direction == ItemTouchHelper.LEFT) {
                val builder = AlertDialog.Builder(adapter.context)
                builder.setTitle("Delete task")
                builder.setMessage("Are you sure you want to delete this task?")
                builder.setPositiveButton("Confirm") { _, _ ->
                    adapter.deleteItem(position)
                }
                builder.setNegativeButton(android.R.string.cancel) { _, _ ->
                    adapter.notifyItemChanged(viewHolder.adapterPosition)
                }
                val dialog = builder.create()
                dialog.show()
            } else {
                adapter.editItem(position)
            }
        }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dx: Float,
        dy: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dx, dy, actionState, isCurrentlyActive)
        val icon: Drawable?
        val background: ColorDrawable
        val itemView = viewHolder.itemView
        val backgroundCornerOffSet = 20
        if (dx > 0) {
            icon = ContextCompat.getDrawable(adapter.context, R.drawable.edit)
            background = ColorDrawable(ContextCompat.getColor(adapter.context, R.color.green))
        } else {
            icon = ContextCompat.getDrawable(adapter.context, R.drawable.delete)
            background = ColorDrawable(Color.RED)
        }
        assert(icon != null)
        val iconMargin = (itemView.height - icon!!.intrinsicHeight) / 2
        val iconTop = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
        val iconButton = iconTop + icon.intrinsicHeight
        if (dx > 0) { //Swiping to the right
            val iconLeft = itemView.left + iconMargin
            val iconRight = itemView.left + iconMargin + icon.intrinsicHeight
            icon.setBounds(iconLeft, iconRight, iconTop, iconButton)
            background.setBounds(
                itemView.left,
                itemView.top,
                itemView.left + dx.toInt() + backgroundCornerOffSet,
                itemView.bottom
            )
        } else if (dx < 0) { //Swiping to the left
            val iconLeft = itemView.right - iconMargin - icon.intrinsicHeight
            val iconRight = itemView.right - iconMargin
            icon.setBounds(iconLeft, iconRight, iconTop, iconButton)
            background.setBounds(
                itemView.right + dx.toInt() - backgroundCornerOffSet,
                itemView.top,
                itemView.right,
                itemView.bottom
            )
        } else {
            background.setBounds(0, 0, 0, 0)
        }
        background.draw(c)
        icon.draw(c)
    }
}
