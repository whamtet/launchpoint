(ns simpleui.launchpoint.web.views.user-pdf
    (:require
      [simpleui.launchpoint.i18n :refer [i18n]]
      [simpleui.launchpoint.pdf :as pdf]
      [simpleui.launchpoint.web.controllers.profile :as profile]
      [simpleui.launchpoint.web.controllers.user :as user]
      [simpleui.launchpoint.web.service.gravatar :as gravatar]
      [simpleui.launchpoint.web.views.components :as components]
      [simpleui.launchpoint.web.views.dashboard :as dashboard]
      [simpleui.launchpoint.web.views.profile.history :refer [months]]
      [simpleui.launchpoint.util :as util]
      [simpleui.response :as response]))

(defn- pic [email]
  ;; TODO - round edges slightly
  [:image {:xscale 0.65
           :yscale 0.65
           :align :center} (gravatar/gravatar-raw email)])

(defn- names [first_name last_name]
  (list
    [:spacer 0.1]
    [:paragraph {:size 20
                 :align :center} first_name " " last_name]))

(defn- description-section [description]
  (list
   [:spacer]
   [:paragraph description]
   [:spacer]))

(defn- work-history [jobs]
  (list
   (util/map-last
    (fn [last? {:keys [title
                       company
                       present
                       from-year from-month
                       to-year to-month
                       description] :as job}]
      (list
       [:paragraph {:size 13} title]
       [:paragraph {:size 12} company]
       [:paragraph
        (if (pos? from-month)
          (list ((months) from-month) " " from-year " - ")
          (list from-year " - "))
        (cond
          present (i18n "Present")
          (pos? to-month)
          (list ((months) to-month) " " to-year)
          :else to-year)]
       [:paragraph (.replace description "\n" " ")]
       [:spacer]))
    jobs)))

(defn- education-history [education]
  (util/map-last
   (fn [last? {:keys [degree institution year]}]
     (list
      [:paragraph degree]
      [:paragraph institution]
      [:paragraph year]
      [:spacer]))
   education))

(defn- header [text]
  (list
   [:paragraph {:size 16} text]
   [:spacer 0.5]))

(defn- profile* [req]
  (let [{:keys [first_name last_name email]} (user/get-user req)
        {:keys [description jobs education]} (profile/get-cv req)]
    [{}
     (pic email)
     (names first_name last_name)
     (description-section description)
     (header (i18n "Work History"))
     (work-history jobs)
     (header (i18n "Education"))
     (education-history education)]))

(defn profile [req]
  (pdf/pdf (profile* req)))
