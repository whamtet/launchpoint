(ns simpleui.launchpoint.web.views.profile
    (:require
      [simpleui.core :as simpleui]
      [simpleui.launchpoint.i18n :refer [i18n]]
      [simpleui.launchpoint.web.controllers.profile :as profile]
      [simpleui.launchpoint.web.controllers.user :as user]
      [simpleui.launchpoint.web.htmx :refer [page-htmx defcomponent]]
      [simpleui.launchpoint.web.views.components :as components]
      [simpleui.launchpoint.web.views.dashboard :as dashboard :refer [gravatar]]
      [simpleui.launchpoint.web.views.profile.history :as profile.history]
      [simpleui.response :as response]))

(defcomponent ^:endpoint pic [req email]
  (if top-level?
    (components/modal "w-1/2"
                      [:div.flex
                       [:div.m-2.border.rounded-lg.inline-block.overflow-hidden
                        [:img {:src (gravatar email)}]]
                       [:div.p-1
                        (components/p (i18n "Profile pics are managed by Gravatar.  Updates take a few minutes to show up."))
                        [:a {:href "https://gravatar.com/profile/avatars/"
                             :target "_blank"
                             :_ "on click add .hidden to #modal"}
                         (components/button (i18n "Edit"))]]])
    [:a#pic {:href ""
             :hx-get "pic"
             :hx-target "#modal"
             :hx-vals {:email email}}
     [:div.m-2.border.rounded-lg.inline-block.overflow-hidden.relative
      ;; hoverable
      [:div {:class "absolute left-0 top-0 w-64 h-64
      hidden justify-center items-center"}
       [:div.text-white.text-4xl (i18n "Edit")]]
      [:img {:src (gravatar email)}]]]))

(defcomponent ^:endpoint import-modal [req command file]
  (case command
        "upload" (println (profile/import-profile req file))
        "modal"
        (components/modal "w-1/2"
                          [:div.p-3
                           (components/h2 (i18n "Import LinkedIn PDF"))
                           [:img.w-60 {:src "/save-to-pdf.png"}]
                           [:input {:type "file"
                                    :name "file"
                                    :accept "application/pdf"
                                    :hx-encoding "multipart/form-data"
                                    :hx-post "import-modal:upload"}]])
        [:a.ml-2 {:href "#"
                  :hx-get "import-modal:modal"
                  :hx-target "#modal"}
         (components/button (i18n "Import from LinkedIn"))]))

(defn- pic-pdf [email]
  [:a
   [:div.m-2.border.rounded-lg.inline-block.overflow-hidden.relative
    [:img {:src (gravatar email)}]]])

(defcomponent ^:endpoint names [req first_name last_name command]
  (when (= "save" command)
        (user/update-names req first_name last_name))
  (case command
        "edit"
        [:form {:hx-post "names:save"}
         [:input {:class "w-32 text-2xl border rounded-lg mr-2 p-1"
                  :name "first_name"
                  :value first_name
                  :required true}]
         [:input {:class "w-32 text-2xl border rounded-lg p-1"
                  :name "last_name"
                  :value last_name
                  :required true}]
         (components/submit-inline (i18n "Save"))]
        [:div {:hx-target "this"}
         [:span.text-2xl first_name " " last_name]
         [:a.ml-2 {:href "#"
                   :hx-get "names:edit"
                   :hx-vals {:first_name first_name
                             :last_name last_name}}
          (components/button (i18n "Edit"))]
         [:a.ml-2 {:href "/api/profile/pdf"
                   :target "_blank"}
          (components/button (i18n "View PDF"))]
         (import-modal req)]))

(defn- names-pdf [first_name last_name]
  [:div
   [:span.text-2xl first_name " " last_name]])

(defcomponent ^:endpoint description-section [req description]
  (if top-level?
    (do (profile/update-description req description) nil)
    [:div
     (components/h3 (i18n "Description"))
     [:textarea
      {:class "border rounded-lg my-3 p-3 w-3/4"
       :hx-post "description-section"
       :hx-trigger "blur,keyup changed delay:0.5s"
       :rows 5
       :name "description"
       :placeholder (i18n "Describe yourself...")} description]]))

(defcomponent ^:endpoint profile [req]
  (let [{:keys [first_name last_name email]} (user/get-user req)
        {:keys [description jobs education]} (profile/get-cv req)]
    [:div.min-h-screen.p-1 {:_ "on click add .hidden to .drop"}
     [:a.absolute.top-3.left-3 {:href "/"}
      [:img.w-24 {:src "/logo.svg"}]]
     (dashboard/main-dropdown first_name)
     [:div {:class "min-h-screen w-2/3 mx-auto"}
      [:div#modal]
      (pic req email)
      (names req first_name last_name nil)
      [:hr.w-96.my-6.border]
      (description-section req description)
      (components/h3 (i18n "Work History"))
      (profile.history/new-job req)
      (profile.history/work-history req jobs false)
      (components/h3 (i18n "Education"))
      (profile.history/new-education req)
      (profile.history/education-history req education false)
      ]]))

(defn profile-pdf [req]
  (let [{:keys [first_name last_name email]} (user/get-user req)
        {:keys [description jobs education]} (profile/get-cv req)]
    [:div.p-1
     [:div {:class "w-2/3 mx-auto"}
      (pic-pdf email)
      (names-pdf first_name last_name)
      [:hr.w-96.my-6.border]
      ;;description
      [:div description]
      (components/h3 (i18n "Work History"))
      (profile.history/work-history req jobs true)
      (components/h3 (i18n "Education"))
      (profile.history/education-history req education true)
      ]]))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (if (-> req :session :id)
       (let [req (assoc req :query-fn query-fn)]
         (page-htmx
          {:css ["/output.css"] :hyperscript? true}
          (profile req)))
       (response/redirect "/")))))
