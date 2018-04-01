package net.ngorham.todolist;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * To Do List
 * ToDoListDivider.java
 * Purpose: Displays a rectangular divider between list items in a RecyclerView
 *
 * @author Neil Gorham
 * @version 1.0 03/14/2018
 */

public class ToDoListDivider extends RecyclerView.ItemDecoration {
    //Private variables
    private Drawable divider;

    //Constructor
    public ToDoListDivider(Drawable divider){
        this.divider = divider;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state){
        super.getItemOffsets(outRect, view, parent, state);
        if(parent.getChildAdapterPosition(view) == 0){
            return;
        }
        outRect.top = divider.getIntrinsicHeight();
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state){
        int dividerLeft = parent.getPaddingLeft();
        int dividerRight = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();
        for(int i = 0; i < childCount -1; i++){
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int dividerTop = child.getBottom() + params.bottomMargin;
            int dividerBottom = dividerTop + divider.getIntrinsicHeight();
            divider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom);
            if ((parent.getChildAdapterPosition(child) == parent.getAdapter().getItemCount() - 1)
                    && parent.getBottom() < dividerBottom) { // this prevent a parent to hide the last item's divider
                parent.setPadding(parent.getPaddingLeft(),
                        parent.getPaddingTop(),
                        parent.getPaddingRight(),
                        dividerBottom - parent.getBottom());
            }
            divider.draw(canvas);
        }
    }
}
