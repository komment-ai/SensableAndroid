package io.sensable.client.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import io.sensable.client.R;

import java.util.HashMap;
import java.util.List;

/**
 * is an adapter that extends BaseExpandableListAdapter and provides functionality
 * for displaying data in an expandable list format. It takes in a context, a list
 * of header titles, and a map of child data in the format of header title, child
 * title. The adapter provides methods for getting child and group objects, as well
 * as inflating views for each item in the list.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    /**
     * retrieves a child object within a group by providing the group position and child
     * position as inputs. It returns the requested child object from the `_listDataChild`
     * array, which is a nested array of child objects, based on the input group and child
     * positions.
     * 
     * @param groupPosition 0-based index of the group within the list data, which is
     * used to access the corresponding child element within the group's list data.
     * 
     * @param childPosititon 2nd index of a inner list within an object, which is being
     * retrieved from the parent list through the function call.
     * 
     * @returns an object of the type specified by its parameter.
     * 
     * The output is an object of type `_listDataChild`, which is an inner class of the
     * outer class `_ListData`. This means that the output is also an inner class, and
     * its properties can be accessed using the same syntax as the outer class.
     * 
     * The output contains a nested list of objects, where each object represents a child
     * element within the group. The nested list is accessed using the method `get`,
     * followed by the name of the list (i.e., `_listDataChild`).
     * 
     * Each element in the nested list has several properties, including its position
     * within the group (stored as an integer), its type (stored as a string), and its
     * value (stored as an object of type `Object`). The properties are accessed using
     * dot notation, such as `.position`, `.type`, and `.value`.
     * 
     * Overall, the output of the `getChild` function provides a way to access and
     * manipulate the child elements within a group in a hierarchical manner.
     */
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    /**
     * returns the position of a child within a group, where the position is the value
     * of the child's `childPosition` field.
     * 
     * @param groupPosition 0-based position of the group within its parent container,
     * which is used to determine the correct child position for retrieval.
     * 
     * @param childPosition 0-based index of a child element within its parent group,
     * which is used to retrieve the unique identifier of that child element.
     * 
     * @returns a long value representing the child position within its group.
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * inflates a list item layout, retrieves the child text from the adapter, and sets
     * it as the text of a TextView within the layout.
     * 
     * @param groupPosition 0-based position of the group within its parent container,
     * which is used to locate the appropriate child view within the group's layout.
     * 
     * @param childPosition 0-based index of the current child element within its parent
     * group, which is used to identify and customize the child view.
     * 
     * @param isLastChild status of the current child view in the list, indicating whether
     * it is the last child in the group or not.
     * 
     * @param convertView preview  of the view that will be displayed for the given
     * groupPosition and childPosition, which is either null or an existing view that has
     * been inflated from a layout file.
     * 
     * 1/ If it is null, then it represents an inflated layout from the specified XML
     * resource (R.layout.list_item).
     * 2/ The `findViewById` method is used to find a view with the ID `lblListItem`
     * within the inflated layout.
     * 3/ The text view found is referred to as `txtListChild`.
     * 
     * @param parent ViewGroup that contains the adapter's layout and provides the context
     * for inflating the child view.
     * 
     * 	- `_context`: The context object for the application.
     * 	- `convertView`: A variable that holds the view to be modified or created. It is
     * a null value by default.
     * 	- `groupPosition`: An integer indicating the group position of the child view
     * being processed.
     * 	- `childPosition`: An integer indicating the child position within the group.
     * 	- `isLastChild`: A boolean value indicating whether the child view is the last
     * child in the group.
     * 	- `parent`: The parent ViewGroup object whose child view needs to be retrieved
     * or created.
     * 
     * @returns a customized view for displaying a piece of text at a specific position
     * in a list.
     * 
     * 	- `convertView`: This is the View object that represents the child element to be
     * displayed in the list. It can be null if no existing view is available, and it
     * will be inflated a new one if needed.
     * 	- `childPosition`: This is the position of the child element within its group.
     * It is an integer value that ranges from 0 to the total number of children in the
     * group - 1.
     * 	- `isLastChild`: This is a boolean value that indicates whether the current child
     * element is the last one in its group. It is set to true if the current element is
     * the last one, and false otherwise.
     * 	- `parent`: This is the ViewGroup object that contains the group of children. It
     * is used to access the parent's layout parameters and other properties.
     */
    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        txtListChild.setText(childText);
        return convertView;
    }

    /**
     * calculates the number of elements in a list of children associated with a specific
     * group position. It takes the position as an argument and returns the number of
     * elements in the list.
     * 
     * @param groupPosition 0-based index of a group in the list data, which is used to
     * access the corresponding children in the list.
     * 
     * @returns the number of elements in a list of children, calculated by traversing
     * the list of children and counting their size.
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    /**
     * returns a specified object within a list data header based on its position.
     * 
     * @param groupPosition 0-based index of a group within the list data header, which
     * is used to retrieve the corresponding group element from the list.
     * 
     * @returns an object of type `_ListDataHeader`.
     * 
     * 	- `_listDataHeader`: This is an instance of a class that represents a list header.
     * It contains metadata about the list, such as the number of elements it contains
     * and the type of data it holds.
     * 	- `groupPosition`: This is an integer parameter that represents the position of
     * the group within the list.
     * 
     * The return value of the function is an object of type `_listDataHeader`, which
     * contains information about the group at the specified position in the list.
     */
    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    /**
     * returns the number of groups in a list based on the size of its header.
     * 
     * @returns the number of groups in the list of data, which is determined by the size
     * of the `_listDataHeader`.
     */
    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    /**
     * returns the group position as a long value.
     * 
     * @param groupPosition 0-based position of a group within the underlying data
     * structure, which is used to retrieve the corresponding group ID returned by the function.
     * 
     * @returns the input parameter `groupPosition`.
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * inflates a layout for a group in a list, sets the group title as the text of a
     * header view, and returns the modified view.
     * 
     * @param groupPosition 0-based index of the group in the adapter's list, which is
     * used to inflate the appropriate layout for the group and set the header text.
     * 
     * @param isExpanded current expansion state of the group, allowing the function to
     * display an appropriate header title based on whether the group is expanded or collapsed.
     * 
     * @param convertView view that will be reused for the current group if it is not
     * null, otherwise a new view will be inflated from the `R.layout.list_group` layout
     * file.
     * 
     * 	- `convertView`: If it is null, an instance of the LayoutInflater class is used
     * to inflate the R.layout.list_group layout file into a new View object.
     * 	- `parent`: The parent ViewGroup of the newly created View.
     * 
     * @param parent ViewGroup that contains the current group being processed, and is
     * used to position the group correctly within its parent container.
     * 
     * 	- `_context`: This represents the context of the class, which is used to access
     * system services such as the layout inflater.
     * 	- `convertView`: This is a view that is reused when the same group is encountered
     * again. If it is null, a new instance of the `R.layout.list_group` layout will be
     * inflated using the `LayoutInflater` service.
     * 	- `viewGroup`: This represents the parent view group of the current group being
     * processed.
     * 
     * @returns a customized view for displaying a list group header, with the group title
     * displayed prominently in bold text.
     * 
     * 	- `convertView`: This is the view that will be used to display the group header.
     * It can be null if no existing view is available.
     * 	- `isExpanded`: A boolean value indicating whether the group is expanded or collapsed.
     * 	- `parent`: The parent ViewGroup that contains the group being displayed.
     * 	- `headerTitle`: The title of the group, which is set as the text of a TextView
     * in the returned view.
     * 	- `R.id.lblListHeader`: The ID of the TextView element in the layout file where
     * the header title is displayed.
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    /**
     * returns `false`, indicating that no stable IDs are present in the related data.
     * 
     * @returns `false`.
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * determines whether a child can be selected based on its position within a group.
     * It returns `true` if the child can be selected, and `false` otherwise.
     * 
     * @param groupPosition 0-based index of the group within which the `childPosition`
     * parameter's child is being evaluated for selectability.
     * 
     * @param childPosition 0-based index of a child element within its parent group in
     * the adapter's dataset.
     * 
     * @returns `true`.
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}