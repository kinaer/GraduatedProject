package com.project.aek.daytoon;

import android.app.Fragment;
import android.content.Context;
import android.icu.util.Measure;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.project.aek.daytoon.widget.StikerAdapter;

/**
 * Created by aek on 2017-01-11.
 */

public class StikerFragment extends Fragment {
    OnGetItemIdListener mCallback;  //콜백을 만든다.

    //액티비티에서 사용할 리스너를 만든다.
    public interface OnGetItemIdListener{
        public void onGetItemId(int id);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //액티비티가 인터페이스를 구현했는지 검사한다.
        try{
            mCallback = (OnGetItemIdListener)context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString() + "OnGetItemIdListener를 구현해야 합니다.");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.layout_stikerview,container,false);
        root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        int width;
        int height;
        root.post(new Runnable() {
            @Override
            public void run() {
                GridView stikerView = (GridView)root.findViewById(R.id.stikerview);
                stikerView.setAdapter(new StikerAdapter(root.getContext(), root.getWidth(), root.getHeight()));
                stikerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        int itemId = (int)parent.getAdapter().getItem(position);    //클릭된거 아이디를 얻어온다.
                        mCallback.onGetItemId(itemId);  //콜백으로 리스너 호출한다.

                    }
                });
            }
        });

        Log.d("프래그먼트","크리에이트뷰");

        return root;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }   //프래그먼트 뷰가 만들어질때 불려짐



}
