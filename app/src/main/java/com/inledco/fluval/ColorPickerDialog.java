// _______  _                          _______  _
//(  ____ \( \      |\     /||\     /|(  ___  )( \
//| (    \/| (      | )   ( || )   ( || (   ) || (
//| (__    | |      | |   | || |   | || (___) || |
//|  __)   | |      | |   | |( (   ) )|  ___  || |
//| (      | |      | |   | | \ \_/ / | (   ) || |
//| )      | (____/\| (___) |  \   /  | )   ( || (____/\
//|/       (_______/(_______)   \_/   |/     \|(_______/
//.---------------------.
//| Fluval Android App  |
//|Coded by Marcel Becks|
//.---------------------.

package com.inledco.fluval;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.inledco.fluval.ColorPickerView.OnColorChangedListener;

public class ColorPickerDialog extends AlertDialog implements ColorPickerView.OnColorChangedListener
{

    private ColorPickerView mColorPicker;

    private ColorPanelView mOldColor;
    private ColorPanelView mNewColor;

    private OnColorChangedListener mListener;

    public ColorPickerDialog ( Context context, int initialColor )
    {
        this( context, initialColor, null );

        init( initialColor );
    }

    public ColorPickerDialog ( Context context, int initialColor, OnColorChangedListener listener )
    {
        super( context );
        mListener = listener;
        init( initialColor );
    }

    private void init ( int color )
    {
        // To fight color branding.
        getWindow().setFormat( PixelFormat.RGBA_8888 );
        setUp( color );
    }

    private void setUp ( int color )
    {
        boolean isLandscapeLayout = false;

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View layout = inflater.inflate( R.layout.dialog_color_picker, null );

        setView( layout );

        //setTitle("Pick a Color");
        // setIcon(android.R.drawable.ic_dialog_info);

        LinearLayout landscapeLayout = (LinearLayout) layout.findViewById( R.id.dialog_color_picker_extra_layout_landscape );

        if ( landscapeLayout != null )
        {
            isLandscapeLayout = true;
        }

        mColorPicker = (ColorPickerView) layout.findViewById( R.id.color_picker_view );
        mOldColor = (ColorPanelView) layout.findViewById( R.id.color_panel_old );
        mNewColor = (ColorPanelView) layout.findViewById( R.id.color_panel_new );

        if ( !isLandscapeLayout )
        {
            ( (LinearLayout) mOldColor.getParent() ).setPadding( Math.round( mColorPicker.getDrawingOffset() ),
                                                                 0,
                                                                 Math.round( mColorPicker.getDrawingOffset() ),
                                                                 0 );
        }
        else
        {
            landscapeLayout.setPadding( 0, 0, Math.round( mColorPicker.getDrawingOffset() ), 0 );
            setTitle( null );
        }

        mColorPicker.setOnColorChangedListener( this );

        mOldColor.setColor( color );
        mColorPicker.setColor( color, true );
    }

    @Override
    public void onColorChanged ( int color )
    {
        mNewColor.setColor( color );

        if ( mListener != null )
        {
            mListener.onColorChanged( color );
        }
    }

    public void setAlphaSliderVisible ( boolean visible )
    {
        mColorPicker.setAlphaSliderVisible( visible );
    }

    public int getColor ()
    {
        return mColorPicker.getColor();
    }
}
