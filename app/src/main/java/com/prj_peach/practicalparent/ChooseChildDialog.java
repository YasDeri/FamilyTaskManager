package com.prj_peach.practicalparent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.prj_peach.practicalparent.model.Child;
import com.prj_peach.practicalparent.model.ChildManager;
import com.prj_peach.practicalparent.model.CoinFlipRecordManager;
import com.prj_peach.practicalparent.model.ImageHelper;
import com.prj_peach.practicalparent.model.Task;
import com.prj_peach.practicalparent.model.TaskManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChooseChildDialog extends AppCompatDialogFragment {

    private View view;
    private ChooseChildDialogListener listener;
    private List<Child> childList;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.layout_choosing_child_dialog, null);

        registerNoChildButton();
        populateChildList();
        registerListClick();

        return builder.setView(view)
                .setTitle(getResources().getString(R.string.coin_dialogTitle))
                .create();
    }

    private void registerNoChildButton() {
        Button noChildBtn = view.findViewById(R.id.ChoosingChild_noneButton);
        noChildBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResultToActivity(ChildManager.getNobody());
                getDialog().dismiss();
            }
        });
    }

    private void populateChildList() {
        ChildManager manager = ChildManager.getInstance();
        CoinFlipRecordManager recordManager = CoinFlipRecordManager.getInstance();

        childList = new ArrayList<>();
        Child current = manager.getNext(recordManager.getLastChildId());
        UUID firstId = current.getID();
        do {
            childList.add(current);
            current = manager.getNext(current.getID());
        } while (!current.getID().equals(firstId));

        ArrayAdapter<Child> adapter = new CustomChildAdapter();
        ListView childListView = view.findViewById(R.id.ChoosingChild_childList);
        childListView.setAdapter(adapter);
    }

    private void sendResultToActivity(Child child) {
        listener.applyChosenChild(child);
    }

    private class CustomChildAdapter extends ArrayAdapter<Child> {
        public CustomChildAdapter() {
            super(getActivity(), R.layout.child_list_item, childList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.child_list_item, parent, false);
            }

            Child currentChild = childList.get(position);

            // set view items
            ImageView avatarImg = itemView.findViewById(R.id.ChildItem_avatar);
            ImageHelper.setImageCropSmall(getContext(), currentChild.getImagePath(), avatarImg, R.drawable.ic_person_black_24dp);
            TextView nameText = itemView.findViewById(R.id.ChildItem_childName);
            nameText.setText(currentChild.getName());
            TextView taskText = itemView.findViewById(R.id.ChildItem_taskCount);
            taskText.setText("");

            return itemView;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (ChooseChildDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ChooseChildDialogListener");
        }
    }

    public interface ChooseChildDialogListener {
        void applyChosenChild(Child chosenChild);
    }

    private void registerListClick() {
        ListView childrenList = view.findViewById(R.id.ChoosingChild_childList);
        childrenList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Child child = childList.get(position);
                sendResultToActivity(child);
                getDialog().dismiss();
            }
        });
    }
}
