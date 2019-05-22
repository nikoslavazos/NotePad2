package gr.teicm.notepad.Model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

import gr.teicm.notepad.R;

public class CustomFileAdapter extends ArrayAdapter<CustomNotes> {

    private Context mContext;
    int mResource;

    public CustomFileAdapter(Context context, int resource, ArrayList<CustomNotes> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent) {
        String name = getItem(position).getName();
        String date = getItem(position).getDate();

        CustomNotes customNotes = new CustomNotes(name,date);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

        TextView noteName = convertView.findViewById(R.id.noteName);
        TextView noteDate = convertView.findViewById(R.id.noteDate);

        noteName.setText(name);
        noteDate.setText(date);
        return convertView;
    }
}
