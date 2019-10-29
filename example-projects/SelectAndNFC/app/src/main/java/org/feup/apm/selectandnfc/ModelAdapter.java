package org.feup.apm.selectandnfc;

import java.util.List;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class ModelAdapter extends ArrayAdapter<Product> {
  private final List<Product> list;
  private final Activity context;

  public ModelAdapter(Activity context, List<Product> list) {
    super(context, R.layout.rowlayout, list);
    this.context = context;
    this.list = list;
  }

  static class ViewHolder {
    protected TextView text;
    protected CheckBox checkbox;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View view = null;
    if (convertView == null) {
      LayoutInflater inflater = context.getLayoutInflater();
      view = inflater.inflate(R.layout.rowlayout, null);
      final ViewHolder viewHolder = new ViewHolder();
      viewHolder.text = (TextView) view.findViewById(R.id.label);
      viewHolder.checkbox = (CheckBox) view.findViewById(R.id.check);
      viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
          Product element = (Product) viewHolder.checkbox.getTag();
          element.selected = buttonView.isChecked();
        }
      });
      view.setTag(viewHolder);
      viewHolder.checkbox.setTag(list.get(position));
    }
    else {
      view = convertView;
      ((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
    }
    ViewHolder holder = (ViewHolder) view.getTag();
    holder.text.setText(list.get(position).name);
    holder.checkbox.setChecked(list.get(position).selected);
    return view;
  }
}
