(ns simpleui.launchpoint.web.views.profile.history
    (:require
      [simpleui.core :as simpleui]
      [simpleui.launchpoint.i18n :refer [i18n i18ns]]
      [simpleui.launchpoint.web.controllers.company :as company]
      [simpleui.launchpoint.web.controllers.profile :as profile]
      [simpleui.launchpoint.web.htmx :refer [defcomponent]]
      [simpleui.launchpoint.web.views.components :as components]
      [simpleui.launchpoint.util :as util]
      [simpleui.response :as response]))

(def hide-search-results
  "if (event.target.id !== 'company-search-box') {htmx.ajax('GET', 'search-results', '#search-results');}")

(defcomponent ^:endpoint search-results [req company]
  (if (>= (count company) 3)
    (if-let [companies (company/search company)]
      [:div#search-results.p-1.border.rounded-lg
       (for [[company src] companies]
         [:a {:href ""
              :hx-post "company-search"
              :hx-vals {:company company :src src}
              :hx-target "#company-search"}
          [:div {:class "flex h-9 items-center hover:bg-slate-100 p-1"}
           [:img.h-full.ml-1.mr-2 {:src (str "/api/company/" src)}]
           [:span company]]])]
      [:div#search-results])
    [:div#search-results]))

(defcomponent ^:endpoint company-search [req company src]
  [:div#company-search
   [:div.p-1 (i18n "Company") [:span.text-red-500 " *"]]
   [:div.p-1
    [:input {:type "hidden" :name "src" :value src}]
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

[:div {:class "w-1/2"}]
(defcomponent ^:endpoint education-edit-modal [req
                                               ^:long-option i
                                               degree institution year
                                               command]
  (case command
        "save"
        (do
          (profile/add-education req i (util/zipm degree institution year))
          response/hx-refresh)
        (components/modal "w-1/2"
                          [:form.p-3 {:hx-post "education-edit-modal:save"
                                      :hx-target "#modal"}
                           [:input {:type "hidden" :name "i" :value i}]
                           [:div.my-2
                            (components/h2
                             (if i (i18n "Edit Education") (i18n "New Education")))]
                           [:div.my-1
                            (components/text
                             (i18n "Degree") "degree" degree :asterisk)]
                           [:div.my-1
                            (components/text
                             (i18n "Institution") "institution" institution :asterisk)]
                           [:div.my-1
                            (components/number
                             (i18n "Year") "year" year :asterisk)]
                           (components/submit (i18n "Save"))
                           ])))

[:div {:class "w-2/3"}]
(defcomponent ^:endpoint job-edit-modal [req
                                         ^:long-option i
                                         title company src
                                         ^:boolean present
                                         ^:long from-year ^:long from-month
                                         ^:long-option to-year ^:long-option to-month
                                         description
                                         command]
  (case command
        "save"
        (do
          (profile/add-job req i (util/zipm title
                                            company src
                                            present
                                            from-year from-month
                                            to-year to-month
                                            description))
          response/hx-refresh)
        (components/modal "w-2/3"
                          [:form.p-3 {:hx-post "job-edit-modal:save"
                                      :hx-target "#modal"
                                      :onclick hide-search-results}
                           [:input {:type "hidden" :name "i" :value i}]
                           [:div.my-2 (components/h2
                                       (if i (i18n "Edit Job") (i18n "New Job")))]
                           ;; job title
                           [:div {:class "my-1 w-1/2"}
                            (components/text
                             (i18n "Job Title") "title" title :asterisk)]
                           ;; company
                           [:div {:class "my-1 w-1/2"}
                            (company-search req company src)]
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

(defcomponent ^:endpoint new-job [req]
  (if top-level?
    (simpleui/apply-component job-edit-modal req)
    [:div.my-3 {:hx-get "new-job"
                :hx-target "#modal"}
     (components/button (i18n "Add Job"))]))

(defcomponent ^:endpoint edit-job [req ^:long i ^:edn job]
  (if top-level?
    (simpleui/apply-component-map job-edit-modal job req i)
    [:span {:hx-get "edit-job"
            :hx-target "#modal"
            :hx-vals {:job (pr-str job) :i i}}
     (components/button (i18n "Edit Job"))]))

(defcomponent ^:endpoint delete-job [req ^:long i job-title]
  (if top-level?
    (do
      (profile/remove-job req i)
      "")
    [:span {:hx-delete "delete-job"
            :hx-vals {:i i}
            :hx-confirm (format (i18n "Delete %s?") job-title)}
     (components/button (i18n "Delete Job"))]))

(defcomponent ^:endpoint new-education [req]
  (if top-level?
    (simpleui/apply-component education-edit-modal req)
    [:div.my-3 {:hx-get "new-education"
                :hx-target "#modal"}
     (components/button (i18n "Add Education"))]))

(defcomponent ^:endpoint edit-education [req ^:long i ^:edn education]
  (if top-level?
    (simpleui/apply-component-map education-edit-modal education req i)
    [:span {:hx-get "edit-education"
            :hx-target "#modal"
            :hx-vals {:eudcation (pr-str education) :i i}}
     (components/button (i18n "Edit Education"))]))

(defcomponent ^:endpoint delete-education [req ^:long i degree]
  (if top-level?
    (do
      (profile/remove-education req i)
      "")
    [:span {:hx-delete "delete-education"
            :hx-vals {:i i}
            :hx-confirm (format (i18n "Delete %s?") degree)}
     (components/button (i18n "Delete Education"))]))

(defcomponent education-history [req education]
  [:div.p-1
   (map-indexed
    (fn [i {:keys [degree institution year] :as education}]
      [:div {:hx-target "this"}
       [:div.my-1.flex.items-center
        (components/h2 degree)
        [:span.m-2.flex (edit-education req i education) (delete-job req i degree)]]
       [:div.my-1
        [:span.text-lg institution]]
       [:div.my-1 year]
       [:hr {:class "border w-1/2"}]])
    education)])

(defcomponent work-history [req jobs]
  [:div.p-1
   (map-indexed
     (fn [i {:keys [title
                    company src
                    present
                    from-year from-month
                    to-year to-month
                    description] :as job}]
       [:div {:hx-target "this"}
        [:div.my-1.flex.items-center
         (components/h2 title)
         [:span.m-2.flex (edit-job req i job) (delete-job req i title)]]
        [:div.flex.items-center.my-1
         [:span.mr-4.text-lg company]
         (when src [:img.w-9 {:src (str "/api/company/" src)}])]
        [:div.my-1
         ((months) from-month) " " from-year " - "
         (if present
           (i18n "Present")
           [:span ((months) to-month) " " to-year])]
        [:div.my-2 description]
        [:hr {:class "border w-1/2"}]])
     jobs)])
