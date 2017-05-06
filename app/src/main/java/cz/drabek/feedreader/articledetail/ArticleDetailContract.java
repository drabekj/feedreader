package cz.drabek.feedreader.articledetail;

import android.database.Cursor;

import cz.drabek.feedreader.BasePresenter;
import cz.drabek.feedreader.BaseView;
import cz.drabek.feedreader.articles.ArticlesContract;
import cz.drabek.feedreader.data.Article;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface ArticleDetailContract {

    interface View extends BaseView<ArticleDetailContract.Presenter> {

    }

    interface Presenter extends BasePresenter {

    }

}
