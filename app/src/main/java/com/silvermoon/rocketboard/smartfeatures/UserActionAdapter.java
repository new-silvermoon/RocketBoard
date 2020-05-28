package com.silvermoon.rocketboard.smartfeatures;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


import com.silvermoon.rocketboard.data.SmartKeyContract;
import com.silvermoon.rocketboard.data.UserAction;
import com.silvermoon.rocketboard.R;
import com.silvermoon.rocketboard.data.UserActionIntentService;

/**
 * Created by faith on 10/5/2017.
 */

public class UserActionAdapter extends RecyclerView.Adapter<UserActionAdapter.UserActionHolder> {


    public interface OnItemClickListener{
        void OnItemClick(int id,String value);
    }

    public UserActionAdapter(Cursor mCursor, Context context) {
        this.mCursor = mCursor;
        this.context = context;
    }

    public class UserActionHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView tvKeyName,tvAppName;
        public CardView cvUserActionList;
        public ImageButton ibDeleteAction;

        public UserActionHolder(View itemView) {
            super(itemView);
            tvKeyName = itemView.findViewById(R.id.tvKeyName);
            tvAppName = itemView.findViewById(R.id.tvAppName);
            cvUserActionList = itemView.findViewById(R.id.cvUserActionList);
            ibDeleteAction = itemView.findViewById(R.id.ibDeleteAction);
            cvUserActionList.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }

    private static final String MODIFY_COMPLETE_BROADCAST ="com.silvermoon.rocketboard.INSERTION_COMPLETE_BROADCAST";

    private Cursor mCursor;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private static final String TAG = UserActionAdapter.class.getSimpleName();
    @Override
    public UserActionHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_action_detail_item,parent,false);

        return new UserActionHolder(view);
    }

    @Override
    public void onBindViewHolder(UserActionHolder holder, int position) {

        if(mCursor!=null){
            final UserAction userAction = getItem(position);
            if(mCursor.moveToPosition(position)){



                holder.tvAppName.setText(Html.fromHtml("<b>App: </b>"+ userAction.appName));
                holder.tvKeyName.setText(Html.fromHtml("<b>Key: </b>"+ userAction.keyName));
            }
            else{
                holder.tvAppName.setText("No User actions");
                holder.tvKeyName.setText("");
            }


            holder.ibDeleteAction.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Log.i(TAG,"Delete button is clicked");
                    Uri deleteURI = ContentUris.withAppendedId(SmartKeyContract.CONTENT_URI,userAction.id);
                    String [] selectionArgs = new String[]{String.valueOf(userAction.id)};
                    UserActionIntentService.deleteUserAction(context,deleteURI,selectionArgs);


                }
            });
        }
        else{
            Log.i(TAG, "onBindViewHolder: Cursor is null");
        }



    }

    @Override
    public int getItemCount() {
        return (mCursor != null) ? mCursor.getCount() : 0;
    }

    public UserAction getItem(int position) {
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("Invalid item position requested");
        }

        return new UserAction(mCursor);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    public void swapCursor(Cursor cursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = cursor;
        notifyDataSetChanged();
    }


}
