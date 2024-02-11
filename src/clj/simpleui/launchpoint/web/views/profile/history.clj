(ns simpleui.launchpoint.web.views.profile.history
    (:require
      [simpleui.launchpoint.i18n :refer [i18n i18ns]]
      [simpleui.launchpoint.web.controllers.company :as company]
      [simpleui.launchpoint.web.controllers.profile :as profile]
      [simpleui.launchpoint.web.htmx :refer [defcomponent]]
      [simpleui.launchpoint.web.views.components :as components]
      [simpleui.launchpoint.util :as util]))

(def hide-search-results
  "if (event.target.id !== 'company-search-box') {htmx.ajax('GET', 'search-results', '#search-results');}")

(defcomponent ^:endpoint search-results [req company]
  (if (>= (count company) 3)
    (if-let [companies (company/search company)]
      [:div#search-results.p-1.border.rounded-lg
       (for [[company src] companies]
         [:a {:href ""
              :hx-post "company-search"
              :hx-vals {:company company}
              :hx-target "#company-search"}
          [:div {:class "flex h-9 items-center hover:bg-slate-100 p-1"}
           [:img.h-full.ml-1.mr-2 {:src (str "/api/company/" src)}]
           [:span company]]])]
      [:div#search-results])
    [:div#search-results]))

(defcomponent ^:endpoint company-search [req company]
  [:div#company-search
   [:div.p-1 (i18n "Company") [:span.text-red-500 " *"]]
   [:div.p-1
    [:input#company-search-box.w-full.p-1
     {:type "text"
      :name "company"
      :value company
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

(defn- month-select [label name value asterisk]
  [:div
   [:div.p-1 label (when asterisk [:span.text-red-500 " *"])]
   [:div.p-1
    [:select {:class "p-1.5 w-full" :name name}
     (map-indexed
      (fn [i month]
        [:option {:value i
                  :selected (= i value)}
         month])
      (months))]]])

(defn- present-checkbox [present?]
  [:div.flex.p-1
   [:input {:class "m-1"
            :type "checkbox"
            :name "present"
            :checked present?
            :hx-get "job-to"
            :hx-target "#job-to"
            :hx-include "#job-to *"}]
   [:div.m-1.ml-3 (i18n "I am currently in this role")]])

(defn- number [label name value asterisk]
  (components/input
    "number"
   {:label label
    :value value
    :asterisk asterisk
    :required asterisk}))

[:div.opacity-50]
(defcomponent ^:endpoint job-to [req ^:long-option to-year ^:long-option to-month ^:boolean present]
  [:fieldset#job-to {:class (if present
                              "flex opacity-50"
                              "flex")
                     :disabled present}
   [:div {:class "w-1/2"}
    (components/number
     (i18n "To Year") "to-year" to-year (not present))]
   [:div {:class "w-1/2"}
    (month-select (i18n "To Month") "to-month" to-month (not present))]])

[:div {:class "w-2/3"}]
(defcomponent ^:endpoint job-edit-modal [req
                                         modal-title
                                         title company
                                         ^:boolean present
                                         ^:long from-year ^:long from-month
                                         ^:long-option to-year ^:long-option to-month
                                         description
                                         command]
  (case command
        "save"
        (do
          (profile/add-job req (util/zipm title
                                          company
                                          present
                                          from-year from-month
                                          to-year to-month
                                          description))
          [:div#modal])
        (components/modal "w-2/3"
                          [:form.p-3 {:hx-post "job-edit-modal:save"
                                      :hx-target "#modal"
                                      :onclick hide-search-results}
                           [:div.my-2 (components/h2 modal-title)]
                           ;; job title
                           [:div {:class "my-1 w-1/2"}
                            (components/text
                             (i18n "Job Title") "title" title :asterisk)]
                           ;; company
                           [:div {:class "my-1 w-1/2"}
                            (company-search req company)]
                           ;; present
                           (present-checkbox present)
                           ;; from
                           [:div.flex
                            [:div {:class "w-1/2"}
                             (components/number
                              (i18n "From Year") "from-year" from-year :asterisk)]
                            [:div {:class "w-1/2"}
                             (month-select (i18n "From Month") "from-month" from-month :asterisk)]]
                           ;; to
                           (job-to req to-year to-month present)
                           ;; description
                           [:textarea.border.rounded-lg.mt-3.w-full.p-2
                            {:rows 5
                             :name "description"
                             :required true
                             :placeholder (i18n "Describe your role...")} description]
                           (components/submit (i18n "Save"))
                           ])))
