package cz.drabek.feedreader.articles;

import android.database.Cursor;

import cz.drabek.feedreader.BasePresenter;
import cz.drabek.feedreader.BaseView;
import cz.drabek.feedreader.data.Article;
import cz.drabek.feedreader.feeds.FeedsContract;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface ArticlesContract {

    interface View extends BaseView<Presenter> {

        void showArticles(Cursor data);

        void showNoArticles();

        void setLoadingIndicator(boolean active);

        void showArticleDetailsUi(Article article);

    }

    interface Presenter extends BasePresenter {

        void loadArticles();

        void openArticleDetails(Article article);

    }

}
