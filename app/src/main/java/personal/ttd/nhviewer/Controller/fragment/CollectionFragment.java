package personal.ttd.nhviewer.Controller.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import personal.ttd.nhviewer.Controller.fragment.base.ComicListFragment;
import personal.ttd.nhviewer.Model.comic.Collection;
import personal.ttd.nhviewer.Model.comic.CollectionMaker;

public class CollectionFragment extends ComicListFragment {
    private int collectionid;

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        collectionid = args.getInt("id");
    }

    @Override
    protected String getActionBarTitle() {
        return Collection.NAME_LIST.get(collectionid);
    }

    @Override
    protected boolean getCanDelete() {
        return true;
    }

    @Override
    protected boolean getHasPage() {
        return false;
    }

    @Override
    protected void setList(int page) {
        Collection c = CollectionMaker.getCollection(collectionid);
        listReturnCallback.onResponse(c.comicList);
    }
}
