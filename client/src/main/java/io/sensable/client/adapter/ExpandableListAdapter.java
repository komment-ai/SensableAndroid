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
 * Is designed to manage expandable lists in Android applications. It extends
 * BaseExpandableListAdapter and provides functionality for displaying data in an
 * expandable list format. The class retrieves child objects based on group positions,
 * calculates the number of children per group, and inflates views for each item in
 * the list.
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
     * Returns an object at a specified position within a sublist associated with a group
     * in a hierarchical list structure. The group is identified by its index, and the
     * child position is determined relative to the sublist.
     *
     * @param groupPosition 0-based index of the group or category for which to retrieve
     * child elements, as specified by the `_listDataHeader` map.
     *
     * @param childPosititon 0-based index of the child item to retrieve within the list
     * of child items associated with the specified `groupPosition`.
     *
     * @returns an object from a list of child data.
     */
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    /**
     * Returns a unique identifier for each child element at a given position within a
     * group. The returned ID is based on the child's relative position within the group,
     * with the first child receiving an ID of 0 and subsequent children incrementing accordingly.
     *
     * @param groupPosition 0-based index of the group or parent element for which the
     * `getChildId` method is called to retrieve the identifier of its child at the
     * specified `childPosition`.
     *
     * @param childPosition 0-based index of the child element being accessed within a
     * group, and its value is returned as the unique identifier for that child.
     *
     * @returns a unique long integer representing each child position.
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * Inflates a layout for a child view within a group, retrieves the child's text
     * content, and sets it to a TextView widget. It returns the converted view with the
     * set text.
     *
     * @param groupPosition 0-based position of the group in the list that contains the
     * child view being inflated, which is used to retrieve the child text from the data
     * source.
     *
     * @param childPosition 0-based index of the child view within its parent group, used
     * to retrieve the corresponding child text and set it in the TextView.
     *
     * @param isLastChild state of whether the child view being accessed is the last one
     * within its group, which can potentially be used to optimize rendering or styling
     * of the child views accordingly.
     *
     * @param convertView view to be recycled and reused, or null if a new view must be
     * created.
     *
     * Inflates to null initially and then inflated with layout R.layout.list_item when
     * not null.
     *
     * @param parent ViewGroup that this view is being added to, serving as a reference
     * for layout and alignment purposes.
     *
     * Parent is a ViewGroup object representing the parent view group. It has no attributes
     * mentioned in this context.
     *
     * @returns a custom list item view with child text.
     *
     * The output is a view representing a child in an expandable list view group. It has
     * a text value set from the `childText` string and is inflated with the layout
     * resource `R.layout.list_item`.
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
     * Returns the number of child elements for a given group position within a list data
     * structure, utilizing a header to index into the child list and retrieve its size.
     *
     * @param groupPosition 0-based index of a group in the list data header, used to
     * retrieve the corresponding child data from the `_listDataChild` map.
     *
     * @returns an integer representing the size of a list.
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    /**
     * Returns an object at a specified position from a list data header. The position
     * is provided as an integer parameter, and the function retrieves the corresponding
     * object from the `_listDataHeader` collection.
     *
     * @param groupPosition 0-based index of the group to be retrieved from the
     * `_listDataHeader` collection, allowing for specific groups to be accessed and returned.
     *
     * @returns an object retrieved from the `_listDataHeader` list.
     */
    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    /**
     * Returns an integer representing the number of items in a list referred to as
     * `_listDataHeader`. This count is used for grouping or categorizing data in a
     * hierarchical manner. The function overrides the default implementation and provides
     * custom behavior for getting group counts.
     *
     * @returns an integer representing the size of a list.
     */
    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    /**
     * Returns a long integer value that corresponds to the specified group position. The
     * group position is provided as an input parameter and is directly returned without
     * any modification or processing. This suggests a simple mapping between group
     * positions and their corresponding IDs.
     *
     * @param groupPosition 0-based index of the group for which to retrieve the corresponding
     * ID, which is directly returned as the result.
     *
     * @returns the integer value of the input `groupPosition`.
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * Returns a view representing a group in a list. It inflates a layout for each group
     * if necessary, sets the text of a header label to the group's title, and returns
     * the converted view. The returned view is used to display the group's title in the
     * list.
     *
     * @param groupPosition 0-based index of the group being rendered, used to retrieve
     * its title and configure the view accordingly.
     *
     * @param isExpanded state of the group at the given position, indicating whether it
     * is currently expanded or not.
     *
     * @param convertView view that will be used to represent the group at the specified
     * `groupPosition`, and it is reused whenever possible for efficiency.
     *
     * convertView is either null or an inflated View object from R.layout.list_group;
     * if convertView is null, it is initialized with the layout inflater; otherwise, its
     * original state remains unchanged.
     *
     * @param parent parent view group that the converted view is being added to or
     * replaced within.
     *
     * Parent is a ViewGroup object representing the parent group to be laid out and
     * shown. It has main properties such as layout_width, layout_height, and layout_margin.
     *
     * @returns a customized group view for an expandable list.
     *
     * It is a View object, specifically a layout view with an inflated design from
     * R.layout.list_group. The view contains a TextView with the ID lblListHeader, which
     * displays the header title in bold font.
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
     * Returns a boolean value indicating whether the IDs returned by the adapter are
     * stable across different configuration changes or not. In this implementation, it
     * always returns `false`, implying that the IDs may change due to configuration changes.
     *
     * @returns a boolean value indicating that IDs are not stable.
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * Returns a boolean value indicating whether the child at the specified position
     * within the group at the specified position can be selected or not. In this
     * implementation, it always returns `true`, allowing any child to be selected.
     *
     * @param groupPosition 0-based index of the group within an ExpandableListView that
     * is being checked for selectability of its child at the specified `childPosition`.
     *
     * @param childPosition 0-based position of a child item within its parent group in
     * a hierarchical data structure.
     *
     * @returns a boolean value indicating whether children are selectable.
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}