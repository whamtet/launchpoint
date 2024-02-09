(ns simpleui.launchpoint.web.views.profile.history
    (:require
      [simpleui.launchpoint.i18n :refer [i18n]]
      [simpleui.launchpoint.web.htmx :refer [defcomponent]]
      [simpleui.launchpoint.web.views.components :as components]))

[:div {:class "w-2/3"}]
(defcomponent job-edit-modal [req modal-title cv] ;; cv is never recalled
  (let [{:keys [title
                company
                from-year from-month
                to-year to-month]} cv]
    (components/modal "w-2/3"
                      [:div.p-3
                       [:div.my-2 (components/h2 modal-title)]
                       [:div {:class "my-1 w-1/2"}
                        (components/text
                         (i18n "Job Title") "title" title :asterisk)]])))
