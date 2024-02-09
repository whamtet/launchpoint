(ns simpleui.launchpoint.web.views.profile.history
    (:require
      [simpleui.launchpoint.i18n :refer [i18n]]
      [simpleui.launchpoint.web.controllers.company :as company]
      [simpleui.launchpoint.web.htmx :refer [defcomponent]]
      [simpleui.launchpoint.web.views.components :as components]))

(def search-hider
  [:div#hide-company-search.hidden.h-1
   {:hx-post "search-results"
    :hx-target "#search-results"}])
(def hide-search-results
  "if (event.target.id !== 'company-search-box') {document.querySelector('#hide-company-search').click();}")

(defcomponent ^:endpoint search-results [req q]
  (if (>= (count q) 3)
    [:div#search-results.p-1.border.rounded-lg
     (for [[company src] (company/search q)]
       [:a {:href ""
            :hx-post "company-search"
            :hx-vals {:q company}
            :hx-target "#company-search"}
        [:div {:class "flex hover:bg-slate-100 p-1"}
         [:img.w-3.mx-1 {:src (str "/api/company/" src)}]
         [:span company]]])]
    [:div#search-results]))

(defcomponent ^:endpoint company-search [req q]
  [:div#company-search
   [:div.p-1 (i18n "Company") [:span.text-red-500 " *"]]
   [:div.p-1
    [:input#company-search-box.w-full.p-1
     {:type "text"
      :name "q"
      :value q
      :required true
      :placeholder (i18n "Company")
      :hx-post "search-results"
      :hx-target "#search-results"
      :hx-trigger "focus,keyup changed delay:0.5s"
      :autocomplete "off"}]
    (search-results req nil)]])

[:div {:class "w-2/3"}]
(defcomponent job-edit-modal [req modal-title cv] ;; this component is never-reevaluated
  (let [{:keys [title
                company
                from-year from-month
                to-year to-month]} cv]
    (components/modal "w-2/3"
                      [:div#job-edit.p-3
                       {:onclick hide-search-results}
                       search-hider
                       [:div.my-2 (components/h2 modal-title)]
                       [:div {:class "my-1 w-1/2"}
                        (components/text
                         (i18n "Job Title") "title" title :asterisk)]
                       [:div {:class "my-1 w-1/2"}
                        (company-search req company)]])))
