package com.example.application.views.productos;

import com.example.application.data.entity.Producto;
import com.example.application.data.service.ProductoService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Productos")
@Route(value = "productos/:productoID?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class ProductosView extends Div implements BeforeEnterObserver {

    private final String PRODUCTO_ID = "productoID";
    private final String PRODUCTO_EDIT_ROUTE_TEMPLATE = "productos/%s/edit";

    private final Grid<Producto> grid = new Grid<>(Producto.class, false);

    private TextField idProducto;
    private TextField nombre;
    private TextField precio;
    private TextField stock;
    private TextField proovedor;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<Producto> binder;

    private Producto producto;

    private final ProductoService productoService;

    public ProductosView(ProductoService productoService) {
        this.productoService = productoService;
        addClassNames("productos-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("idProducto").setAutoWidth(true);
        grid.addColumn("nombre").setAutoWidth(true);
        grid.addColumn("precio").setAutoWidth(true);
        grid.addColumn("stock").setAutoWidth(true);
        grid.addColumn("proovedor").setAutoWidth(true);
        grid.setItems(query -> productoService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(PRODUCTO_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(ProductosView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Producto.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(idProducto).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("idProducto");
        binder.forField(precio).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("precio");
        binder.forField(stock).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("stock");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.producto == null) {
                    this.producto = new Producto();
                }
                binder.writeBean(this.producto);
                productoService.update(this.producto);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(ProductosView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> productoId = event.getRouteParameters().get(PRODUCTO_ID).map(Long::parseLong);
        if (productoId.isPresent()) {
            Optional<Producto> productoFromBackend = productoService.get(productoId.get());
            if (productoFromBackend.isPresent()) {
                populateForm(productoFromBackend.get());
            } else {
                Notification.show(String.format("The requested producto was not found, ID = %s", productoId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(ProductosView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        idProducto = new TextField("Id Producto");
        nombre = new TextField("Nombre");
        precio = new TextField("Precio");
        stock = new TextField("Stock");
        proovedor = new TextField("Proovedor");
        formLayout.add(idProducto, nombre, precio, stock, proovedor);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Producto value) {
        this.producto = value;
        binder.readBean(this.producto);

    }
}
