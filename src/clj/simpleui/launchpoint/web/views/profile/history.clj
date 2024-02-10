(ns simpleui.launchpoint.web.views.profile.history
    (:require
      [simpleui.launchpoint.i18n :refer [i18n i18ns]]
      [simpleui.launchpoint.web.controllers.company :as company]
      [simpleui.launchpoint.web.htmx :refer [defcomponent]]
      [simpleui.launchpoint.web.views.components :as components]))

(def hide-search-results
  "if (event.target.id !== 'company-search-box') {htmx.ajax('GET', 'search-results', '#search-results');}")

(defcomponent ^:endpoint search-results [req q]
  (if (>= (count q) 3)
    (if-let [companies (company/search q)]
      [:div#search-results.p-1.border.rounded-lg
       (for [[company src] companies]
         [:a {:href ""
              :hx-post "company-search"
              :hx-vals {:q company}
              :hx-target "#company-search"}
          [:div {:class "flex h-9 items-center hover:bg-slate-100 p-1"}
           [:img.h-full.ml-1.mr-2 {:src (str "/api/company/" src)}]
           [:span company]]])]
      [:div#search-results])
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

(defn months [] (i18ns
                 "January"
                 "February"
                 "March"
                 "April"
                 "May"
                 "June"
                 "July"
                 "August"
                 "September"
                 "October"
                 "November"
                 "December"))

(defn month-select [label name value]
  [:div
   [:div.p-1 label [:span.text-red-500 " *"]]
   [:div.p-1
    [:select {:class "p-1.5 w-full" :name name}
     (map-indexed
      (fn [i month]
        [:option {:value i
                  :selected (= month value)}
         month])
      (months))]]])

[:div {:class "w-2/3"}]
(defcomponent job-edit-modal [req
                              modal-title
                              title company
                              ^:long from-year ^:long from-month
                              ^:long to-year ^:long to-month
                              description]
  (components/modal "w-2/3"
                    [:form.p-3 {:onclick hide-search-results}
                     [:div.my-2 (components/h2 modal-title)]
                     ;; job title
                     [:div {:class "my-1 w-1/2"}
                      (components/text
                       (i18n "Job Title") "title" title :asterisk)]
                     ;; company
                     [:div {:class "my-1 w-1/2"}
                      (company-search req company)]
                     ;; from
                     [:div.flex
                      [:div {:class "w-1/2"}
                       (components/number
                        (i18n "From Year") "from-year" from-year :asterisk)]
                      [:div {:class "w-1/2"}
                       (month-select (i18n "From Month") "from-month" from-month)]]
                     ]))
