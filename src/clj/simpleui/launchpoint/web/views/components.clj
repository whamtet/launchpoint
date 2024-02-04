(ns simpleui.launchpoint.web.views.components)

(defn input
  ([type {:keys [label name value autocomplete asterisk]}]
   [:div
    [:div.p-1 label (when asterisk [:span.text-red-500 " *"])]
    [:div.p-1
     [:input.w-full.p-1
      {:type type
       :name name
       :value value
       :autocomplete autocomplete
       :required true
       :placeholder label}]]])
  ([type label name]
   (input type {:label label :name name}))
  ([type label name value]
   (input type {:label label :name name :value value}))
  ([type label name value asterisk]
   (input type {:label label :name name :value value :asterisk asterisk})))

(def text (partial input "text"))
(def email (partial input "email"))

(defn password
  ([label name]
   (input "password" label name))
  ([label name asterisk]
   (input "password" {:label label
                      :name name
                      :asterisk asterisk
                      :autocomplete "off"})))

(defn hidden [name value]
  [:input {:type "hidden" :name name :value value}])

(defn submit [label]
  [:div.p-1
   [:input {:type "submit"
            :class "bg-clj-blue p-1.5 rounded-lg text-white w-24"
            :value label}]])
