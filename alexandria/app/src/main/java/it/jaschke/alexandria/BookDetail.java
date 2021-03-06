package it.jaschke.alexandria;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.services.DownloadImage;


public class BookDetail extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{

    public static final String EAN_KEY = "EAN";
    private final int LOADER_ID = 10;
    private View rootView;
    private String ean;
    private String bookTitle;
    private ShareActionProvider shareActionProvider;

    public BookDetail()
        {
        }

    @Override
    public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            Log.e("bookdetails", "onCreate");

        }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState)
        {
            Log.e("bookdetails", "onCreateView");
            setHasOptionsMenu(true);
            setRetainInstance(false);
            Bundle arguments = getArguments();
            if (arguments != null)
                {
                    ean = arguments.getString(BookDetail.EAN_KEY);
                    getLoaderManager().restartLoader(LOADER_ID, null, this);
                }

            rootView = inflater.inflate(R.layout.fragment_full_book, container, false);
            rootView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                    {
                        Intent bookIntent = new Intent(getActivity(), BookService.class);
                        bookIntent.putExtra(BookService.EAN, ean);
                        bookIntent.setAction(BookService.DELETE_BOOK);
                        getActivity().startService(bookIntent);
                        getActivity().getFragmentManager().popBackStack();
                    }
            });
            // to hide keyboard
            ((MainActivity)getActivity()).hideKeyboard();
            return rootView;
        }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
        {
            menu.clear();
            inflater.inflate(R.menu.book_detail, menu);

            MenuItem menuItem = menu.findItem(R.id.action_share);
            shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
            Log.e("bookdetails", "" + (shareActionProvider == null));
        }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
        {
            return new CursorLoader(
                    getActivity(),
                    AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(ean)),
                    null,
                    null,
                    null,
                    null
            );
        }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
        {
            if (!data.moveToFirst())
                {
                    return;
                }

            bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
            ((TextView) rootView.findViewById(R.id.fullBookTitle)).setText(bookTitle);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + bookTitle);
//            shareActionProvider.setShareIntent(shareIntent);

            String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry
                    .SUBTITLE));
            ((TextView) rootView.findViewById(R.id.fullBookSubTitle)).setText(bookSubTitle);

            String desc = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.DESC));
            ((TextView) rootView.findViewById(R.id.fullBookDesc)).setText(desc);

            String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry
                    .AUTHOR));
            String[] authorsArr = authors.split(",");
            ((TextView) rootView.findViewById(R.id.authors)).setLines(authorsArr.length);
            ((TextView) rootView.findViewById(R.id.authors)).setText(authors.replace(",", "\n"));
            String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry
                    .IMAGE_URL));
            if (Patterns.WEB_URL.matcher(imgUrl).matches())
                {
                    new DownloadImage((ImageView) rootView.findViewById(R.id.fullBookCover))
                            .execute(imgUrl);
                    rootView.findViewById(R.id.fullBookCover).setVisibility(View.VISIBLE);
                }

            String categories = data.getString(data.getColumnIndex(AlexandriaContract
                    .CategoryEntry.CATEGORY));
            ((TextView) rootView.findViewById(R.id.categories)).setText(categories);

            if (rootView.findViewById(R.id.right_container) != null)
                {
                    rootView.findViewById(R.id.backButton).setVisibility(View.INVISIBLE);
                }

        }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
        {

        }

    @Override
    public void onPause()
        {
            super.onPause();
//            if (MainActivity.IS_TABLET && rootView.findViewById(R.id.right_container) == null)
//                {
//                    getActivity().getFragmentManager().popBackStack();
//                }
//            super.onDestroyView();
        }

    @Override
    public void onDestroyView()
        {
            super.onDestroyView();
            getLoaderManager().destroyLoader(LOADER_ID);
        }
}